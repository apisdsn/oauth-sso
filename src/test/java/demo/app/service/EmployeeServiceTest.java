package demo.app.service;

import demo.app.entity.Address;
import demo.app.entity.Employee;
import demo.app.model.EmployeeRequest;
import demo.app.repository.EmployeeRepository;
import demo.app.validator.ValidationHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class EmployeeServiceTest {
    @Mock
    private ValidationHelper validationHelper;
    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private AddressService addressService;
    @Mock
    private ReimbursementService reimbursementService;
    @Mock
    private OAuth2AuthenticatedPrincipal principal;

    @InjectMocks
    private EmployeeService employeeService;
    private EmployeeRequest employeeRequest;
    private Employee employee;
    private Address address;

    @BeforeEach
    public void setup() {
        employeeRequest = new EmployeeRequest();
        employee = new Employee();
        address = new Address();
        employeeRequest.setFullName("test");
        employee.setReimbursements(new ArrayList<>());

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("sub", "123");
        attributes.put("email", "test@test.com");
        when(principal.getAttributes()).thenReturn(attributes);
    }

    @Test
    void testRegisterWhenEmployeeRequestFullNameIsNullThenThrowResponseStatusException() {
        employeeRequest.setFullName("");
        validationHelper.validate(employeeRequest);
        assertThrows(ResponseStatusException.class, () -> employeeService.register(employeeRequest, principal));
    }

    @Test
    void testRegisterWhenEmployeeRequestIsNullThenThrowResponseStatusException() {
        validationHelper.validate((Object) null);
        assertThrows(ResponseStatusException.class, () -> employeeService.register(null, principal));
    }

    @Test
    void testRegisterWhenEmployeeExistsThenThrowResponseStatusException() {
        validationHelper.validate(employeeRequest);
        when(employeeRepository.existsByClientId(anyString())).thenReturn(true);
        assertThrows(ResponseStatusException.class, () -> employeeService.register(employeeRequest, principal));
    }

    @Test
    void testRegisterWhenEmployeeDoesNotExistThenSave() {
        validationHelper.validate(employeeRequest);
        when(employeeRepository.existsByClientId(anyString())).thenReturn(false);
        employeeService.register(employeeRequest, principal);
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    void testGetCurrentWhenEmployeeExistsThenReturnEmployee() {
        when(employeeRepository.findByClientId(anyString())).thenReturn(Optional.of(employee));
        employeeService.getCurrent(principal);
        verify(employeeRepository, times(1)).findByClientId(anyString());
    }

    @Test
    void testGetByClientIdWhenEmployeeExistsThenReturnEmployee() {
        when(employeeRepository.findByClientId(anyString())).thenReturn(Optional.of(employee));
        employeeService.getByClientId("123");
        verify(employeeRepository, times(1)).findByClientId(anyString());
    }

    @Test
    void testFindAllEmployeeThenReturnAllEmployees() {
        when(employeeRepository.findAll()).thenReturn(Collections.singletonList(employee));
        employeeService.findAllEmployee();
        verify(employeeRepository, times(1)).findAll();

        assertEquals(1, employeeService.findAllEmployee().size());
        assertNotNull(employee);
    }

    @Test
    void testUpdateWhenEmployeeExistsThenSave() {
        when(employeeRepository.findByClientId(anyString())).thenReturn(Optional.of(employee));
        employeeService.update(employeeRequest, principal);
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    void testRemoveCurrentWhenEmployeeExistsThenDelete() {
        when(employeeRepository.findByClientId(anyString())).thenReturn(Optional.of(employee));
        employeeService.removeCurrent(principal);
        verify(employeeRepository, times(1)).delete(any(Employee.class));
    }

    @Test
    void testRemoveByClientIdWhenEmployeeExistsThenDelete() {
        when(employeeRepository.findByClientId(anyString())).thenReturn(Optional.of(employee));
        employeeService.removeByClientId("123");
        verify(employeeRepository, times(1)).delete(any(Employee.class));
    }
}
