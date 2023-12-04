package demo.app.it.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import demo.app.entity.Employee;
import demo.app.model.EmployeeRequest;
import demo.app.model.EmployeeResponse;
import demo.app.model.WebResponse;
import demo.app.repository.EmployeeRepository;
import demo.app.service.EmployeeService;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EmployeeApiIntegrationTest {
    private static HttpHeaders headers;
    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    public static void init() {
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth("WCIUyf1_uaqPiVgz4OyqJRks0YT-ybNb7DisPIKYxE1kBuBt687IY_5yrcU3z9I05wJDXTY");
    }

    @Test
    @Sql(statements = "DELETE FROM employees WHERE client_id = '239414077758111751'", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void testRegisterEmployeeAndReturn201HttpStatus() throws JsonProcessingException {
        EmployeeRequest employeeRequest = new EmployeeRequest();
        employeeRequest.setFullName("John Doe");
        employeeRequest.setGender("Laki-Laki");
        employeeRequest.setCompany("PT Cinta Sejati");
        employeeRequest.setPosition("Software Engineer");
        employeeRequest.setPhoneNumber("081234567890");
        employeeRequest.setProvince("Indonesia");
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(employeeRequest), headers);

        ResponseEntity<WebResponse<String>> response = restTemplate.exchange(createURLWithPort() + "/register", HttpMethod.POST, entity, new ParameterizedTypeReference<>() {
        });

        WebResponse<String> result = response.getBody();
        assertNotNull(result);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Data has been stored in the database", result.getData());
        assertNull(result.getErrors());
    }

    @Test
    @Sql(statements = "INSERT INTO employees(employee_id, client_id, email, full_name, phone_number, gender, company, position) VALUES (1, '239414077758111751', 'user@i2dev.com', 'John Doe', '089512611411', 'Laki-Laki', 'PT Cinta Sejati', 'Software Engineer')", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "DELETE FROM employees WHERE client_id = '239414077758111751'", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void testGetCurrentEmployeeAndReturn200HttpStatus() {
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        ResponseEntity<WebResponse<EmployeeResponse>> response = restTemplate.exchange(createURLWithPort() + "/current", HttpMethod.GET, entity, new ParameterizedTypeReference<>() {
        });

        WebResponse<EmployeeResponse> result = response.getBody();
        assertNotNull(result);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, result.getData().getEmployeeId());
        assertEquals("239414077758111751", result.getData().getClientId());
        assertEquals("user@i2dev.com", result.getData().getEmail());
        assertEquals("John Doe", result.getData().getFullName());
        assertEquals("089512611411", result.getData().getPhoneNumber());
        assertEquals("Laki-Laki", result.getData().getGender());
        assertEquals("PT Cinta Sejati", result.getData().getCompany());
        assertEquals("Software Engineer", result.getData().getPosition());
        assertEquals("Laki-Laki", result.getData().getGender());
        assertEquals("239414077758111751", employeeService.getByClientId("239414077758111751").getClientId());
        Optional<Employee> employeeOptional = employeeRepository.findByClientId("239414077758111751");
        employeeOptional.map(Employee::getFullName)
                .ifPresent(fullName -> assertEquals(result.getData().getFullName(), fullName));
        assertThat(result.getData().getReimbursements()).isEmpty();
        assertNull(result.getData().getAddress());
        assertNull(result.getErrors());
    }

    @Test
    @Sql(statements = "INSERT INTO employees(employee_id, client_id, email, full_name, phone_number, gender, company, position) VALUES (1, '239414077758111751', 'user@i2dev.com', 'John Doe', '089512611411', 'Laki-Laki', 'PT Cinta Sejati', 'Software Engineer')", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "DELETE FROM employees WHERE client_id = '239414077758111751'", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void testUpdateCurrentEmployeeAndReturn200HttpStatus() throws JsonProcessingException {
        EmployeeRequest employeeRequest = new EmployeeRequest();
        employeeRequest.setFullName("Alex Benjamin");
        employeeRequest.setPhoneNumber("089512611412");
        employeeRequest.setGender("Laki-Laki");
        employeeRequest.setCompany("PT Melia Sejaterah");
        employeeRequest.setPosition("Driver");
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(employeeRequest), headers);

        ResponseEntity<WebResponse<EmployeeResponse>> response = restTemplate.exchange(createURLWithPort() + "/current", HttpMethod.PUT, entity, new ParameterizedTypeReference<>() {
        });

        WebResponse<EmployeeResponse> result = response.getBody();
        assertNotNull(result);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, result.getData().getEmployeeId());
        assertEquals("239414077758111751", result.getData().getClientId());
        assertEquals("user@i2dev.com", result.getData().getEmail());
        assertEquals("Alex Benjamin", result.getData().getFullName());
        assertEquals("089512611412", result.getData().getPhoneNumber());
        assertEquals("Laki-Laki", result.getData().getGender());
        assertEquals("PT Melia Sejaterah", result.getData().getCompany());
        assertEquals("Driver", result.getData().getPosition());
        assertEquals("Laki-Laki", result.getData().getGender());
        assertEquals("239414077758111751", employeeService.getByClientId("239414077758111751").getClientId());
        Optional<Employee> employeeOptional = employeeRepository.findByClientId("239414077758111751");
        employeeOptional.map(Employee::getFullName)
                .ifPresent(fullName -> assertEquals(result.getData().getFullName(), fullName));
        assertThat(result.getData().getReimbursements()).isEmpty();
        assertNull(result.getData().getAddress());
        assertNull(result.getErrors());
    }

    @Test
    @Sql(statements = "INSERT INTO employees(employee_id, client_id, email, full_name, phone_number, gender, company, position) VALUES (1, '239414077758111751', 'user@i2dev.com', 'John Doe', '089512611411', 'Laki-Laki', 'PT Cinta Sejati', 'Software Engineer')", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "DELETE FROM employees WHERE client_id = '239414077758111751'", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void testRemoveCurrentEmployeeAndReturn200HttpStatus() {
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<WebResponse<String>> response = restTemplate.exchange(createURLWithPort() + "/current", HttpMethod.DELETE, entity, new ParameterizedTypeReference<>() {
        });

        WebResponse<String> result = response.getBody();
        assertNotNull(result);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Data has been removed from the database", result.getData());
        assertNull(result.getErrors());
    }

    private String createURLWithPort() {
        return "http://localhost:" + port + "/api/employees";
    }
}