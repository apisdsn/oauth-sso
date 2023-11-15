package demo.app.repository;

import demo.app.entity.Employee;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Transactional
public class EmployeeRepositoryTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Test
    public void testExistsByClientIdWhenEmployeeExistsThenReturnTrue() {
        // Arrange
        Employee employee = new Employee();
        employee.setClientId("123");
        employee.setEmail("john.doe@example.com");
        employee.setFullName("John Doe");
        employee.setCompany("Acme Corporation");
        employee.setPosition("Software Engineer");
        employee.setGender("Male");
        employeeRepository.save(employee);

        // Act
        boolean exists = employeeRepository.existsByClientId("123");

        // Assert
        assertTrue(exists);
    }

    @Test
    public void testExistsByClientIdWhenEmployeeDoesNotExistThenReturnFalse() {
        // Arrange
        // No employee with clientId "123" is saved

        // Act
        boolean exists = employeeRepository.existsByClientId("123");

        // Assert
        assertFalse(exists);
    }

    @Test
    public void testFindByClientIdWhenEmployeeExistsThenReturnEmployee() {
        // Arrange
        Employee employee = new Employee();
        employee.setClientId("123");
        employee.setEmail("john.doe@example.com");
        employee.setFullName("John Doe");
        employee.setCompany("Acme Corporation");
        employee.setPosition("Software Engineer");
        employee.setGender("Male");
        employeeRepository.save(employee);

        // Act
        Optional<Employee> foundEmployee = employeeRepository.findByClientId("123");

        // Assert
        assertTrue(foundEmployee.isPresent());
        assertEquals(employee.getClientId(), foundEmployee.get().getClientId());
        assertEquals(employee.getEmail(), foundEmployee.get().getEmail());
        assertEquals(employee.getFullName(), foundEmployee.get().getFullName());
        assertEquals(employee.getCompany(), foundEmployee.get().getCompany());
        assertEquals(employee.getPosition(), foundEmployee.get().getPosition());
        assertEquals(employee.getGender(), foundEmployee.get().getGender());
    }

    @Test
    public void testFindByClientIdWhenEmployeeDoesNotExistThenReturnEmptyOptional() {
        // Arrange
        // No employee with clientId "123" is saved

        // Act
        Optional<Employee> foundEmployee = employeeRepository.findByClientId("123");

        // Assert
        assertFalse(foundEmployee.isPresent());
    }
}
