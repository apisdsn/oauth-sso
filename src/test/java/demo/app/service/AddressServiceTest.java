package demo.app.service;

import demo.app.entity.Address;
import demo.app.entity.Employee;
import demo.app.model.AddressRequest;
import demo.app.model.AddressResponse;
import demo.app.repository.AddressRepository;
import demo.app.repository.EmployeeRepository;
import demo.app.validator.ValidationHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class AddressServiceTest {
    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private AddressRepository addressRepository;
    @Mock
    private ValidationHelper validationHelper;
    @InjectMocks
    private AddressService addressService;

    private AddressRequest addressRequest;
    private OAuth2AuthenticatedPrincipal principal;
    private Employee employee;
    private Address address;

    @BeforeEach
    public void setUp() {
        addressRequest = new AddressRequest();
        addressRequest.setStreet("123 Street");
        addressRequest.setCity("City");
        addressRequest.setProvince("Province");
        addressRequest.setCountry("Country");
        addressRequest.setPostalCode("12345");

        principal = mock(OAuth2AuthenticatedPrincipal.class);

        address = new Address();
        address.setAddressId("addressId");
        address.setStreet("123 Street");
        address.setCity("City");
        address.setProvince("Province");
        address.setCountry("Country");
        address.setPostalCode("12345");

        employee = new Employee();
        employee.setClientId("clientId");
        employee.setAddress(address);
    }

    @Test
    public void testUpdateAddressWhenAddressIsUpdatedThenReturnUpdatedAddress() {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("sub", "clientId");
        when(principal.getAttributes()).thenReturn(attributes);
        when(employeeRepository.findByClientId("clientId")).thenReturn(Optional.of(employee));
        when(addressRepository.save(address)).thenReturn(address);


        AddressResponse response = addressService.updateAddress(addressRequest, principal);

        assertEquals(address.getAddressId(), response.getAddressId());
        assertEquals(address.getStreet(), response.getStreet());
        assertEquals(address.getCity(), response.getCity());
        assertEquals(address.getProvince(), response.getProvince());
        assertEquals(address.getCountry(), response.getCountry());
        assertEquals(address.getPostalCode(), response.getPostalCode());
    }

    @Test
    public void testUpdateAddressWhenAddressNotFoundThenThrowResponseStatusException() {
        employee.setAddress(null);
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("sub", "clientId");
        when(principal.getAttributes()).thenReturn(attributes);
        when(employeeRepository.findByClientId("clientId")).thenReturn(Optional.of(employee));

        assertThrows(ResponseStatusException.class, () -> addressService.updateAddress(addressRequest, principal));
    }

    @Test
    public void testToAddressResponse() {
        AddressResponse response = addressService.toAddressResponse(address);

        assertEquals(address.getAddressId(), response.getAddressId());
        assertEquals(address.getStreet(), response.getStreet());
        assertEquals(address.getCity(), response.getCity());
        assertEquals(address.getProvince(), response.getProvince());
        assertEquals(address.getCountry(), response.getCountry());
        assertEquals(address.getPostalCode(), response.getPostalCode());
    }
}
