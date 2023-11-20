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
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
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
    @Mock
    private OAuth2AuthenticatedPrincipal principal;
    private Employee employee;


    @BeforeEach
    public void setUp() {
        employeeRequest = new EmployeeRequest();
//        principal = mock(OAuth2AuthenticatedPrincipal.class);
        employee = new Employee();
        employeeRequest.setFullName("test");
        employee.setReimbursements(new ArrayList<>());
    }

    @Test
    public void testCreateEmployeeWhenValidRequestThenEmployeeCreated() {
        given(principal.getAttributes()).willReturn(Map.of("sub", "123", "email", "test@test.com"));
        given(employeeRepository.existsByClientId(anyString())).willReturn(false);

        employeeService.register(employeeRequest, principal);
        verify(validationHelper, times(1)).validate(employeeRequest);
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    public void testCreateEmployeeWhenFullNameBlankInvalidRequestThenBadRequest() {
        employeeRequest.setFullName("");
        given(principal.getAttributes()).willReturn(Map.of("sub", "123", "email", "test@test.com"));
        given(employeeRepository.existsByClientId(anyString())).willReturn(false);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> employeeService.register(employeeRequest, principal));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Full name cannot be blank", exception.getReason());

        verify(validationHelper, times(1)).validate(employeeRequest);
        verify(employeeRepository, times(0)).save(any(Employee.class));
    }

    @Test
    public void testCreateEmployeeWhenNullRequestThenBadRequest() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> employeeService.register(null, principal));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Request cannot be null", exception.getReason());

        verify(validationHelper, times(0)).validate(employeeRequest);
        verify(employeeRepository, times(0)).save(any(Employee.class));
    }

    @Test
    public void testGetEmployeeByIdWhenValidClientIdThenEmployeeReturned() {
        given(principal.getAttributes()).willReturn(Map.of("sub", "123"));
        given(employeeRepository.findByClientId(anyString())).willReturn(Optional.of(employee));

        EmployeeResponse employeeResponse = employeeService.getCurrent(principal);
        assertNotNull(employeeResponse);
        verify(employeeRepository, times(1)).findByClientId(anyString());
    }

    @Test
    public void testGetEmployeeByIdWhenInvalidClientIdThenNotFound() {
        given(principal.getAttributes()).willReturn(Map.of("sub", "123"));
        given(employeeRepository.findByClientId(anyString())).willReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> employeeService.getCurrent(principal));
        verify(employeeRepository, times(1)).findByClientId(anyString());
    }

    @Test
    public void testGetAllEmployeesThenAllEmployeesReturned() {
        given(employeeRepository.findAll()).willReturn(List.of(employee));

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