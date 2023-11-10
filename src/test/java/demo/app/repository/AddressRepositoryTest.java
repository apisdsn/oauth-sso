package demo.app.repository;

import demo.app.entity.Address;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class AddressRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AddressRepository addressRepository;

    private Address address;

    @BeforeEach
    public void setUp() {
        address = new Address();
        address.setAddressId("123");
        address.setStreet("Test Street");
        address.setCity("Test City");
        address.setProvince("Test Province");
        address.setCountry("Test Country");
        address.setPostalCode("12345");
        entityManager.persist(address);
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
