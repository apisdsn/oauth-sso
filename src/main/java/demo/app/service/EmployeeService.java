package demo.app.service;

import demo.app.entity.Address;
import demo.app.entity.Employee;
import demo.app.model.EmployeeRequest;
import demo.app.model.EmployeeResponse;
import demo.app.repository.EmployeeRepository;
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

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class EmployeeService {
    @Autowired
    private ValidationHelper validationHelper;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private AuthoritiesManager authoritiesManager;
    @Autowired
    private AddressService addressService;

    @Transactional
    public void register(EmployeeRequest request, OAuth2AuthenticatedPrincipal principal, Authentication auth) {
        validateAuthorization(principal, auth);
        validationHelper.validate(request);
        String clientId = getClientIdFromPrincipal(principal);
        String email = getEmailFromPrincipal(principal);
        log.debug("clientId: {}", clientId);
        log.debug("email: {}", email);

        if (employeeRepository.existsByClientId(clientId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Employee already exists");
        }

        Employee employee = new Employee();
        employee.setClientId(clientId);
        employee.setFullName(request.getFullName());
        employee.setPhoneNumber(request.getPhoneNumber());
        employee.setEmail(email);
        employee.setCompany(request.getCompany());
        employee.setPosition(request.getPosition());
        employee.setGender(request.getGender());

        Address address = new Address();
        address.setAddressId(UUID.randomUUID().toString());
        address.setStreet(request.getStreet());
        address.setCity(request.getCity());
        address.setProvince(request.getProvince());
        address.setCountry(request.getCountry());
        address.setPostalCode(request.getPostalCode());

        employee.setAddress(address);
        address.setEmployee(employee);

        employeeRepository.save(employee);
    }

    @Transactional(readOnly = true)
    public EmployeeResponse getCurrent(OAuth2AuthenticatedPrincipal principal, Authentication auth) {
        validateAuthorization(principal, auth);
        String clientId = getClientIdFromPrincipal(principal);
        log.debug("clientId: {}", clientId);
        Employee employee = findEmployeeByClientId(clientId);
        return toEmployeeResponse(employee);
    }

    // admin or manager service
    @Transactional(readOnly = true)
    public EmployeeResponse getByClientId(String clientId, OAuth2AuthenticatedPrincipal principal, Authentication auth) {
        if (authoritiesManager.checkIfUserIsAdminOrManager(principal)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not authorized to perform this operation");
        }
        validateAuthorization(principal, auth);

        Employee employee = findEmployeeByClientId(clientId);
        return toEmployeeResponse(employee);
    }

    // admin or manager service
    @Transactional(readOnly = true)
    public List<EmployeeResponse> findAllEmployee(OAuth2AuthenticatedPrincipal principal, Authentication auth) {
        if (authoritiesManager.checkIfUserIsAdminOrManager(principal)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not authorized to perform this operation");
        }
        validateAuthorization(principal, auth);
        List<Employee> employees = employeeRepository.findAll();
        return employees.stream().map(this::toEmployeeResponse).toList();
    }

    @Transactional
    public EmployeeResponse update(EmployeeRequest request, OAuth2AuthenticatedPrincipal principal) {
        validationHelper.validate(request);

        String clientId = getClientIdFromPrincipal(principal);
        Employee employee = findEmployeeByClientId(clientId);

        employee.setFullName(request.getFullName());
        employee.setPhoneNumber(request.getPhoneNumber());
        employee.setCompany(request.getCompany());
        employee.setPosition(request.getPosition());
        employee.setGender(request.getGender());
        employeeRepository.save(employee);
        return toEmployeeResponse(employee);
    }

    public void removeCurrent(OAuth2AuthenticatedPrincipal principal, Authentication auth) {
        validateAuthorization(principal, auth);
        String clientId = getClientIdFromPrincipal(principal);
        Employee employee = findEmployeeByClientId(clientId);
        if (employee != null) {
            employeeRepository.delete(employee);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found for the given clientId");
        }
    }

    public void removeByClientId(String clientId, OAuth2AuthenticatedPrincipal principal, Authentication auth) {
        validateAuthorization(principal, auth);
        if (authoritiesManager.checkIfUserIsAdminOrManager(principal)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not authorized to perform this operation");
        }
        Employee employee = findEmployeeByClientId(clientId);
        if (employee != null) {
            employeeRepository.delete(employee);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found for the given clientId");
        }
    }

    private EmployeeResponse toEmployeeResponse(Employee employee) {
        return EmployeeResponse.builder()
                .employeeId(employee.getEmployeeId())
                .clientId(employee.getClientId())
                .fullName(employee.getFullName())
                .phoneNumber(employee.getPhoneNumber())
                .email(employee.getEmail())
                .company(employee.getCompany())
                .position(employee.getPosition())
                .gender(employee.getGender())
                .address(addressService.toAddressResponse(employee.getAddress()))
                .build();
    }

    private String getClientIdFromPrincipal(OAuth2AuthenticatedPrincipal principal) {
        Map<String, Object> attributes = principal.getAttributes();
        if (attributes == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Client ID not found");
        }
        return attributes.get("sub").toString();
    }

    private String getEmailFromPrincipal(OAuth2AuthenticatedPrincipal principal) {
        Map<String, Object> attributes = principal.getAttributes();
        if (attributes == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Email not found");
        }
        return attributes.get("email").toString();
    }

    private Employee findEmployeeByClientId(String clientId) {
        return employeeRepository.findByClientId(clientId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found"));
    }

    private void validateAuthorization(OAuth2AuthenticatedPrincipal principal, Authentication auth) {
        if (authoritiesManager.hasAuthority(principal, auth)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not authorized");
        }
    }
}
