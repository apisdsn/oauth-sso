package demo.app.repository;

import demo.app.entity.Address;
import demo.app.entity.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Transactional
public class AddressRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    private Address address;

    @BeforeEach
    public void setUp() {
        addressRepository.deleteAll();
        employeeRepository.deleteAll();
        Employee employee = new Employee();
        employee.setClientId("123");
        employee.setEmail("john.doe@example.com");
        employee.setFullName("John Doe");
        employee.setCompany("Acme Corporation");
        employee.setPosition("Software Engineer");
        employee.setGender("Male");

        address = new Address();
        address.setAddressId("123");
        address.setStreet("Test Street");
        address.setCity("Test City");
        address.setProvince("Test Province");
        address.setCountry("Test Country");
        address.setPostalCode("12345");

        employee.setAddress(address);
        address.setEmployee(employee);

        entityManager.persist(employee);
        entityManager.flush();
    }

    @Test
    public void testFindFirstByAddressIdWhenAddressExistsThenReturnAddress() {
        // Act
        Optional<Address> found = addressRepository.findFirstByAddressId(address.getAddressId());

        // Assert
        assertThat(found.isPresent()).isTrue();
        assertThat(found.get()).isEqualTo(address);
    }

    @Test
    public void testFindFirstByAddressIdWhenAddressDoesNotExistThenReturnEmptyOptional() {
        // Act
        Optional<Address> found = addressRepository.findFirstByAddressId("nonexistent");

        // Assert
        assertThat(found.isPresent()).isFalse();
    }
}