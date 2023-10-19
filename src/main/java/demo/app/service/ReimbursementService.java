package demo.app.service;

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
    public ReimbursementResponse create(ReimbursementRequest request, OAuth2AuthenticatedPrincipal principal, Authentication auth) {
        validateAuthorization(principal, auth);
        validationHelper.validate(request);

        String clientId = getClientIdFromPrincipal(principal);
        Employee employee = findEmployeeByClientId(clientId);


        Reimbursement reimbursement = new Reimbursement();
        reimbursement.setEmployee(employee);
        reimbursement.setAmount(request.getAmount());
        reimbursement.setDescription(request.getDescription());
        reimbursement.setStatus(false);
        reimbursement.setDateCreated(LocalDateTime.now());


        reimbursementRepository.save(reimbursement);

        return toReimbursementResponse(reimbursement);
    }


    public ReimbursementResponse updateReimbursementUser(Long reimbursementId, ReimbursementRequest request, OAuth2AuthenticatedPrincipal principal, Authentication auth) {
        validateAuthorization(principal, auth);
        validationHelper.validate(request);
        Employee employee = employeeRepository.findByClientId(getClientIdFromPrincipal(principal))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee is not found"));

        Reimbursement reimbursement = reimbursementRepository.findFirstByEmployeeAndReimbursementId(employee, reimbursementId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reimbursement is not found"));

        if (request.getAmount() != null) {
            reimbursement.setAmount(request.getAmount());
        }

        if (request.getDescription() != null) {
            reimbursement.setDescription(request.getDescription());
        }

        reimbursement.setDateCreated(LocalDateTime.now());

        reimbursementRepository.save(reimbursement);

        return toReimbursementResponse(reimbursement);
    }

    // admin or manager service
    @Transactional
    public ReimbursementResponse updateReimbursementByAdmin(Long reimbursementId, ReimbursementRequest request, OAuth2AuthenticatedPrincipal principal, Authentication auth) {
        validateAuthorization(principal, auth);
        validationHelper.validate(request);
        if (authoritiesManager.checkIfUserIsAdminOrManager(principal)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not authorized to perform this operation");
        }

        String idApprovedBy = getClientIdFromPrincipal(principal);

        Reimbursement reimbursement = reimbursementRepository.findById(reimbursementId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reimbursement not found"));

        reimbursement.setStatus(request.getStatus());
        reimbursement.setApprovedId(idApprovedBy);
        reimbursement.setDateUpdated(LocalDateTime.now());

        reimbursementRepository.save(reimbursement);

        return toReimbursementResponse(reimbursement);
    }

    @Transactional
    public void removeReimbursementByUser(Long reimbursementId, OAuth2AuthenticatedPrincipal principal, Authentication auth) {
        validateAuthorization(principal, auth);
        Employee employee = employeeRepository.findByClientId(getClientIdFromPrincipal(principal))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee is not found"));

        Reimbursement reimbursement = reimbursementRepository.findFirstByEmployeeAndReimbursementId(employee, reimbursementId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reimbursement is not found"));


        reimbursementRepository.delete(reimbursement);

    }

    // admin or manager service
    @Transactional(readOnly = true)
    public List<ReimbursementResponse> getReimbursementsWithStatusFalse(OAuth2AuthenticatedPrincipal principal, Authentication auth) {
        validateAuthorization(principal, auth);
        if (authoritiesManager.checkIfUserIsAdminOrManager(principal)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not authorized to perform this operation");
        }
        validateAuthorization(principal, auth);

        List<Reimbursement> reimbursements = reimbursementRepository.findByStatusFalse();

        return reimbursements.stream()
                .map(this::toReimbursementResponse)
                .collect(Collectors.toList());
    }

    public ReimbursementResponse toReimbursementResponse(Reimbursement reimbursement) {
        return ReimbursementResponse.builder()
                .reimbursementId(reimbursement.getReimbursementId())
                .employeeId(reimbursement.getEmployee().getEmployeeId())
                .amount(convertRupiah(reimbursement.getAmount()))
                .approvedId(reimbursement.getApprovedId())
                .description(reimbursement.getDescription())
                .status(reimbursement.getStatus())
                .dateCreated(reimbursement.getDateCreated())
                .dateUpdated(reimbursement.getDateUpdated())
                .build();
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
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found for the given clientId" + clientId));
    }

    public String convertRupiah(BigDecimal bigDecimalPrice) {
        Locale localId = new Locale("in", "ID");
        NumberFormat formatter = NumberFormat.getCurrencyInstance(localId);
        return formatter.format(bigDecimalPrice);
    }
}
