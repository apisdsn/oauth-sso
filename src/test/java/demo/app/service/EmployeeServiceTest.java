package demo.app.service;

import demo.app.entity.Employee;
import demo.app.model.EmployeeRequest;
import demo.app.model.EmployeeResponse;
import demo.app.repository.EmployeeRepository;
import demo.app.validator.ValidationHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTest {
    @Mock
    private ValidationHelper validationHelper;
    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private ReimbursementService reimbursementService;
    @InjectMocks
    private EmployeeService employeeService;

    private EmployeeRequest employeeRequest;
    private OAuth2AuthenticatedPrincipal principal;
    private Employee employee;


    @BeforeEach
    public void setUp() {
        employeeRepository.deleteAll();
        
        employeeRequest = new EmployeeRequest();
        principal = mock(OAuth2AuthenticatedPrincipal.class);
        employee = new Employee();
        employeeRequest.setFullName("test");
        employee.setReimbursements(new ArrayList<>());
    }

    @Test
    public void testCreateEmployeeWhenValidRequestThenEmployeeCreated() {
        when(principal.getAttributes()).thenReturn(Map.of("sub", "123", "email", "test@test.com"));
        when(employeeRepository.existsByClientId(anyString())).thenReturn(false);

        employeeService.register(employeeRequest, principal);
        verify(validationHelper, times(1)).validate(employeeRequest);
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    public void testCreateEmployeeWhenNullRequestThenBadRequest() {
        assertThrows(ResponseStatusException.class, () -> employeeService.register(null, principal));
    }

    @Test
    public void testGetEmployeeByIdWhenValidClientIdThenEmployeeReturned() {
        when(principal.getAttributes()).thenReturn(Map.of("sub", "123"));
        when(employeeRepository.findByClientId(anyString())).thenReturn(Optional.of(employee));

        EmployeeResponse employeeResponse = employeeService.getCurrent(principal);
        assertNotNull(employeeResponse);
        verify(employeeRepository, times(1)).findByClientId(anyString());
    }

    @Test
    public void testGetEmployeeByIdWhenInvalidClientIdThenNotFound() {
        when(principal.getAttributes()).thenReturn(Map.of("sub", "123"));
        when(employeeRepository.findByClientId(anyString())).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> employeeService.getCurrent(principal));
    }

    @Test
    public void testGetAllEmployeesThenAllEmployeesReturned() {
        when(employeeRepository.findAll()).thenReturn(List.of(employee));

        List<EmployeeResponse> employeeResponses = employeeService.findAllEmployee();

        assertNotNull(employeeResponses);
        assertEquals(1, employeeResponses.size());
        verify(employeeRepository, times(1)).findAll();
    }

    @Test
    public void testUpdateEmployeeWhenValidRequestThenEmployeeUpdated() {
        when(principal.getAttributes()).thenReturn(Map.of("sub", "123"));
        when(employeeRepository.findByClientId(anyString())).thenReturn(Optional.of(employee));

        EmployeeResponse employeeResponse = employeeService.update(employeeRequest, principal);

        assertNotNull(employeeResponse);
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    public void testUpdateEmployeeWhenNullRequestThenBadRequest() {
        assertThrows(ResponseStatusException.class, () -> employeeService.update(null, principal));
    }

    @Test
    public void testDeleteEmployeeWhenValidClientIdThenEmployeeDeleted() {
        when(principal.getAttributes()).thenReturn(Map.of("sub", "123"));
        when(employeeRepository.findByClientId(anyString())).thenReturn(Optional.of(employee));

        employeeService.removeCurrent(principal);

        verify(employeeRepository, times(1)).delete(any(Employee.class));
    }

    @Test
    public void testDeleteEmployeeWhenInvalidClientIdThenNotFound() {
        when(principal.getAttributes()).thenReturn(Map.of("sub", "123"));
        when(employeeRepository.findByClientId(anyString())).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> employeeService.removeCurrent(principal));
    }
}