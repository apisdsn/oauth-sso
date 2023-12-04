package demo.app.it.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import demo.app.entity.Address;
import demo.app.model.AddressRequest;
import demo.app.model.AddressResponse;
import demo.app.model.WebResponse;
import demo.app.repository.AddressRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AddressApiIntegrationTest {
    private static HttpHeaders headers;
    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    public static void init() {
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth("WCIUyf1_uaqPiVgz4OyqJRks0YT-ybNb7DisPIKYxE1kBuBt687IY_5yrcU3z9I05wJDXTY");
    }

    @Test
    @Sql(statements = "INSERT INTO employees(employee_id, client_id, email, full_name, phone_number, gender, company, position) VALUES (1, '239414077758111751', 'user@i2dev.com', 'John Doe', '089512611411', 'Laki-Laki', 'PT Cinta Sejati', 'Software Engineer')", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "INSERT INTO address(address_id, employee_id, street, city, province, country, postal_code) VALUES ('test', 1, 'JL Kenangan No. 1', 'Surabaya', 'Jawa Timur', 'Indonesia', '12332')", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "DELETE FROM employees WHERE client_id = '239414077758111751'", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void testUpdateCurrentAddressAndReturn200HttpStatus() throws JsonProcessingException {
        AddressRequest addressRequest = new AddressRequest();
        addressRequest.setStreet("Jl Majapahit No. 15");
        addressRequest.setCity("Jakarta");
        addressRequest.setCountry("Indonesia");
        addressRequest.setProvince("DKI Jakarta");
        addressRequest.setPostalCode("12345");
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(addressRequest), headers);

        ResponseEntity<WebResponse<AddressResponse>> response = restTemplate.exchange(createURLWithPort() + "/current", HttpMethod.PUT, entity, new ParameterizedTypeReference<>() {
        });

        WebResponse<AddressResponse> result = response.getBody();

        assertNotNull(result);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("test", result.getData().getAddressId());
        assertEquals("Jakarta", result.getData().getCity());
        assertEquals("Jl Majapahit No. 15", result.getData().getStreet());
        assertEquals("Indonesia", result.getData().getCountry());
        assertEquals("DKI Jakarta", result.getData().getProvince());
        assertEquals("12345", result.getData().getPostalCode());
        Optional<Address> optionalAddress = addressRepository.findFirstByAddressId("test");
        optionalAddress.map(Address::getStreet)
                .ifPresent(street -> assertEquals(result.getData().getStreet(), street));
        assertNull(result.getErrors());
    }

    private String createURLWithPort() {
        return "http://localhost:" + port + "/api/address";
    }
}
