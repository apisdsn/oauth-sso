package demo.app.repository;

import demo.app.entity.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
        // Arrange
        when(employeeRepository.existsByClientId("123")).thenReturn(true);

        // Act
        boolean exists = employeeRepository.existsByClientId("123");

        // Assert
        assertTrue(exists);
        verify(employeeRepository, times(1)).existsByClientId("123");
    }

    @Test
    public void testExistsByClientIdWhenClientIdDoesNotExistThenReturnFalse() {
        // Arrange
        when(employeeRepository.existsByClientId("456")).thenReturn(false);

        // Act
        boolean exists = employeeRepository.existsByClientId("456");

        // Assert
        assertFalse(exists);
        verify(employeeRepository, times(1)).existsByClientId("456");
    }

    @Test
    public void testFindByClientIdWhenClientIdExistsThenReturnOptionalEmployee() {
        // Arrange
        when(employeeRepository.findByClientId("123")).thenReturn(Optional.of(employee));

        // Act
        Optional<Employee> foundEmployee = employeeRepository.findByClientId("123");

        // Assert
        assertTrue(foundEmployee.isPresent());
        assertEquals(employee.getClientId(), foundEmployee.get().getClientId());
        verify(employeeRepository, times(1)).findByClientId("123");
    }

    @Test
    public void testFindByClientIdWhenClientIdDoesNotExistThenReturnEmptyOptional() {
        // Arrange
        when(employeeRepository.findByClientId("456")).thenReturn(Optional.empty());

        // Act
        Optional<Employee> foundEmployee = employeeRepository.findByClientId("456");

        // Assert
        assertTrue(foundEmployee.isEmpty());
        verify(employeeRepository, times(1)).findByClientId("456");
    }
}