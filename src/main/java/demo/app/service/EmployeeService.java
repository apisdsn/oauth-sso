package demo.app.service;

import demo.app.entity.Address;
import demo.app.entity.Employee;
import demo.app.model.EmployeeRequest;
import demo.app.model.EmployeeResponse;
import demo.app.repository.AddressRepository;
import demo.app.repository.EmployeeRepository;
import demo.app.utils.AuthoritiesManager;
import demo.app.validator.ValidationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2TokenIntrospectionClaimNames;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class EmployeeService {
    private final ValidationService validationService;
    private final AuthoritiesManager authoritiesManager;
    private final EmployeeRepository employeeRepository;

    @Autowired
    public EmployeeService(ValidationService validationService, AuthoritiesManager authoritiesManager, EmployeeRepository employeeRepository) {
        this.validationService = validationService;
        this.authoritiesManager = authoritiesManager;
        this.employeeRepository = employeeRepository;
    }

    @Transactional
    public void register(EmployeeRequest employeeRequest, OAuth2AuthenticatedPrincipal principal, Authentication auth) {
        log.debug("Auth Authorities: {}", auth.getAuthorities().toString());
        log.debug("Auth Principal: {}", auth.getPrincipal().toString());
        log.debug("Auth Details: {}", auth.getDetails().toString());
        log.debug("Auth Credentials: {}", auth.getCredentials().toString());
        log.debug("Authorities Principal: {}", principal.getAuthorities().toString());

        if (authoritiesManager.hasAuthority(principal, auth)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not authorized");
        }

        validationService.validate(employeeRequest);

        String clientId = principal.getAttribute(OAuth2TokenIntrospectionClaimNames.CLIENT_ID);
        String email = getEmailFromPrincipal(principal);

        if (employeeRepository.existsByClientId(clientId)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Employee already exists");
        }

        Employee employee = new Employee();
        employee.setClientId(clientId);
        employee.setFullName(employeeRequest.getFullName());
        employee.setPhoneNumber(employeeRequest.getPhoneNumber());
        employee.setEmail(email);
        employee.setCompany(employeeRequest.getCompany());
        employee.setPosition(employeeRequest.getPosition());
        employee.setGender(employeeRequest.getGender());

        Address address = new Address();
        address.setAddressId(UUID.randomUUID().toString());
        address.setStreet(employeeRequest.getStreet());
        address.setCity(employeeRequest.getCity());
        address.setProvince(employeeRequest.getProvince());
        address.setCountry(employeeRequest.getCountry());
        address.setPostalCode(employeeRequest.getPostalCode());

        employee.setAddress(address);

        employeeRepository.save(employee);

    }
    @Transactional(readOnly = true)
    public EmployeeResponse getCurrent(OAuth2AuthenticatedPrincipal principal, Authentication auth) {
        if (authoritiesManager.hasAuthority(principal, auth)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not authorized");
        }
        String clientId = getClientIdFromPrincipal(principal);

        Employee employee = employeeRepository.findById(clientId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Employee not found"));

        return toEmployeeResponse(employee);
    }

    public EmployeeResponse getEmployeeById(String employeeId){
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Employee not found"));
        return toEmployeeResponse(employee);
    }

    @Transactional(readOnly = true)
    public List<EmployeeResponse> findAllEmployee(OAuth2AuthenticatedPrincipal principal, Authentication auth) {
        log.debug("Authorities Authentication: {}", auth.getAuthorities().toString());
        log.debug("Authorities Principal: {}", principal.getAuthorities().toString());
        if (authoritiesManager.hasAuthority(principal, auth)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not authorized");
        }
        List<Employee> employees = employeeRepository.findAll();
        return employees.stream().map(this::toEmployeeResponse).collect(Collectors.toList());
    }
    @Transactional
    public EmployeeResponse update(EmployeeRequest request, OAuth2AuthenticatedPrincipal principal){
        validationService.validate(request);

        String clientId = getClientIdFromPrincipal(principal);

        Employee employee = employeeRepository.findById(clientId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Employee not found"));

        employee.setFullName(request.getFullName());
        employee.setPhoneNumber(request.getPhoneNumber());
        employee.setCompany(request.getCompany());
        employee.setPosition(request.getPosition());
        employee.setGender(request.getGender());
        employeeRepository.save(employee);
        return toEmployeeResponse(employee);
    }

    private EmployeeResponse toEmployeeResponse(Employee employee) {
        return EmployeeResponse.builder()
                .clientId(employee.getClientId())
                .fullName(employee.getFullName())
                .phoneNumber(employee.getPhoneNumber())
                .email(employee.getEmail())
                .company(employee.getCompany())
                .position(employee.getPosition())
                .gender(employee.getGender())
                .address(employee.getAddress())
                .build();

    }
    private String getClientIdFromPrincipal(OAuth2AuthenticatedPrincipal principal) {
        Map<String, Object> attributes = principal.getAttributes();
        if (attributes == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Client ID not found");
        }
        return attributes.get("client_id").toString();
    }

    private String getEmailFromPrincipal(OAuth2AuthenticatedPrincipal principal) {
        Map<String, Object> attributes = principal.getAttributes();
        if (attributes == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email not found");
        }
        return attributes.get("email").toString();
    }


}
