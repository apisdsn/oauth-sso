package demo.app.repository;

import demo.app.entity.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ActiveProfiles("test")
@Transactional
public class EmployeeRepositoryTest {
    @Mock
    private EmployeeRepository employeeRepository;
    private Employee employee;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        employee = new Employee();
        employee.setClientId("123");
        employee.setFullName("John Doe");
        employee.setEmail("john.doe@example.com");
    }

    @Test
    public void testExistsByClientIdWhenClientIdExistsThenReturnTrue() {
        given(employeeRepository.existsByClientId("123")).willReturn(true);

        boolean exists = employeeRepository.existsByClientId("123");

        assertTrue(exists);
        verify(employeeRepository, times(1)).existsByClientId("123");
    }

    @Test
    public void testExistsByClientIdWhenClientIdDoesNotExistThenReturnFalse() {
        given(employeeRepository.existsByClientId("456")).willReturn(false);

        boolean exists = employeeRepository.existsByClientId("456");

        assertFalse(exists);
        verify(employeeRepository, times(1)).existsByClientId("456");
    }

    @Test
    public void testFindByClientIdWhenClientIdExistsThenReturnOptionalEmployee() {
        given(employeeRepository.findByClientId("123")).willReturn(Optional.of(employee));

        Optional<Employee> foundEmployee = employeeRepository.findByClientId("123");

        assertTrue(foundEmployee.isPresent());
        assertEquals(employee.getClientId(), foundEmployee.get().getClientId());
        verify(employeeRepository, times(1)).findByClientId("123");
    }

    @Test
    public void testFindByClientIdWhenClientIdDoesNotExistThenReturnEmptyOptional() {
        given(employeeRepository.findByClientId("456")).willReturn(Optional.empty());

        Optional<Employee> foundEmployee = employeeRepository.findByClientId("456");

        assertTrue(foundEmployee.isEmpty());
        verify(employeeRepository, times(1)).findByClientId("456");
    }
}