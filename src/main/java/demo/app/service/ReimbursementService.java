package demo.app.service;

import com.nimbusds.oauth2.sdk.util.StringUtils;
import demo.app.entity.Employee;
import demo.app.entity.Reimbursement;
import demo.app.model.ReimbursementRequest;
import demo.app.model.ReimbursementResponse;
import demo.app.repository.EmployeeRepository;
import demo.app.repository.ReimbursementRepository;
import demo.app.utils.AuthoritiesManager;
import demo.app.validator.ValidationHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReimbursementService {

    @Autowired
    private AuthoritiesManager authoritiesManager;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private ReimbursementRepository reimbursementRepository;
    @Autowired
    private ValidationHelper validationHelper;

    @Transactional
    public ReimbursementResponse createOrUpdateReimbursement(ReimbursementRequest request, OAuth2AuthenticatedPrincipal principal, Authentication auth, String clientId, boolean isAdminOrManager) {
        validateAuthorization(principal, auth);

        validationHelper.validate(request);

        if (!isAdminOrManager && StringUtils.isBlank(clientId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Client ID is required for user update");
        }

        // Jika admin atau manager, gunakan clientId dari path variabel
        String targetClientId = isAdminOrManager ? clientId : getClientIdFromPrincipal(principal);

        Employee employee = findEmployeeByClientId(targetClientId);

        Reimbursement reimbursement = new Reimbursement();
        reimbursement.setEmployee(employee);
        reimbursement.setAmount(request.getAmount());
        reimbursement.setCurrency(request.getCurrency());
        reimbursement.setDescription(request.getDescription());
        
        // admin or manager service
        if (isAdminOrManager) {
            if (authoritiesManager.checkIfUserIsAdminOrManager(principal)) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not authorized to perform this operation");
            }
            reimbursement.setApproved(String.valueOf(employee.getEmployeeId()));
            reimbursement.setStatus(request.getStatus());
            reimbursement.setDateUpdated(LocalDateTime.now());
        } else {
            reimbursement.setDateCreated(LocalDateTime.now());
        }

        return toReimbursementResponse(reimbursementRepository.save(reimbursement));
    }

    @Transactional
    public ReimbursementResponse deleteReimbursementByUser(OAuth2AuthenticatedPrincipal principal, Authentication auth) {
        validateAuthorization(principal, auth);

        String clientId = getClientIdFromPrincipal(principal);
        Employee employee = findEmployeeByClientId(clientId);

        Reimbursement reimbursement = findReimbursementByEmployee(employee);
        if (reimbursement == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Reimbursement not found for the employee");
        }

        reimbursementRepository.delete(reimbursement);

        return toReimbursementResponse(reimbursement);
    }

    // admin or manager service
    @Transactional
    public ReimbursementResponse deleteReimbursementByAdmin(String clientId, OAuth2AuthenticatedPrincipal principal, Authentication auth) {
        if (authoritiesManager.checkIfUserIsAdminOrManager(principal)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not authorized to perform this operation");
        }
        validateAuthorization(principal, auth);

        Employee employee = findEmployeeByClientId(clientId);

        Reimbursement reimbursement = findReimbursementByEmployee(employee);
        if (reimbursement == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Reimbursement not found for the employee");
        }

        reimbursementRepository.delete(reimbursement);

        return toReimbursementResponse(reimbursement);
    }

    // admin or manager service
    @Transactional(readOnly = true)
    public List<ReimbursementResponse> getReimbursementsWithStatusFalse(OAuth2AuthenticatedPrincipal principal, Authentication auth) {
        if (authoritiesManager.checkIfUserIsAdminOrManager(principal)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not authorized to perform this operation");
        }
        validateAuthorization(principal, auth);

        List<Reimbursement> reimbursements = reimbursementRepository.findByStatusFalse();

        return reimbursements.stream()
                .map(this::toReimbursementResponse)
                .collect(Collectors.toList());
    }

    private ReimbursementResponse toReimbursementResponse(Reimbursement reimbursement) {
        NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        return ReimbursementResponse.builder()
                .reimbursementId(reimbursement.getReimbursementId())
                .amount(new BigDecimal(reimbursement.getAmount().toString()))
                .approved(reimbursement.getApproved())
                .currency(reimbursement.getCurrency())
                .description(reimbursement.getDescription())
                .status(reimbursement.getStatus())
                .dateCreated(reimbursement.getDateCreated())
                .dateUpdated(reimbursement.getDateUpdated())
                .build();
    }

    private Reimbursement findReimbursementByEmployee(Employee employee) {
        return reimbursementRepository.findByEmployee(employee)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reimbursement not found for the employee"));
    }

    private String getClientIdFromPrincipal(OAuth2AuthenticatedPrincipal principal) {
        Map<String, Object> attributes = principal.getAttributes();
        if (attributes == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Client ID not found");
        }
        return attributes.get("sub").toString();
    }

    private void validateAuthorization(OAuth2AuthenticatedPrincipal principal, Authentication auth) {
        if (authoritiesManager.hasAuthority(principal, auth)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not authorized");
        }
    }

    private Employee findEmployeeByClientId(String clientId) {
        return employeeRepository.findByClientId(clientId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found"));
    }
}
