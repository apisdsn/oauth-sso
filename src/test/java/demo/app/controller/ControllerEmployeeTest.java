package demo.app.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import demo.app.entity.Address;
import demo.app.entity.Employee;
import demo.app.model.AddressResponse;
import demo.app.model.EmployeeRequest;
import demo.app.model.EmployeeResponse;
import demo.app.model.WebResponse;
import demo.app.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.IOException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ControllerEmployeeTest {


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private String token;

    @BeforeEach
    void setUp() {
        employeeRepository.deleteAll();
        token = "ufs6Oiqp1-JyhvP8sFM8_7c2gq0F_3cysAKGGCzPoNqahGs4ZUuD1dBrneCYgfjdhE0Faw8";
    }

    @Test
    void registerEmployeeSuccessWithToken() throws Exception {
        EmployeeRequest employeeRequest = createEmployeeRequest();
        mockMvc.perform(
                post("/api/employees/register")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeeRequest))
        ).andExpectAll(
                status().isCreated()
        ).andDo(result -> {
            WebResponse<String> response = readValue(result, new TypeReference<>() {
            });
            assertNull(response.getErrors());
            assertEquals("Data has been stored in the database", response.getData());
        });
    }

    @Test
    void registerEmployeeBadRequest() throws Exception {
        EmployeeRequest employeeRequest = createEmployeeRequestWithEmptyFields();
        createSampleEmployee();

        mockMvc.perform(
                post("/api/employees/register")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeeRequest))
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<String> response = readValue(result, new TypeReference<>() {
            });
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void registerEmployeeUnauthorizedTokenNull() throws Exception {
        mockMvc.perform(
                post("/api/employees/register")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + null)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)

        ).andExpect(status().isUnauthorized());
    }

    @Test
    void getEmployeeCurrent() throws Exception {
        Employee employee = createSampleEmployee();

        mockMvc.perform(
                get("/api/employees/current")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<EmployeeResponse> response = readValue(result, new TypeReference<>() {
            });
            assertEmployeeResponseEquals(employee, response.getData());
        });
    }

    @Test
    void getEmployeeUnauthorizedTokenNull() throws Exception {
        mockMvc.perform(
                get("/api/employees/current")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + null)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isUnauthorized());
    }

    @Test
    void updateEmployeeSuccessWithToken() throws Exception {
        Employee employee = createSampleEmployee();
        EmployeeRequest employeeRequest = createEmployeeRequest();

        mockMvc.perform(
                put("/api/employees/current")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeeRequest))

        ).andExpectAll(status().isOk()).andDo(result -> {
            WebResponse<EmployeeResponse> response = readValue(result, new TypeReference<>() {
            });
            assertEmployeeResponseEquals(employee, response.getData());
        });
    }

    @Test
    void updateEmployeeUnauthorizedTokenNull() throws Exception {
        mockMvc.perform(
                put("/api/employees/current")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + null)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)

        ).andExpect(status().isUnauthorized());
    }

    @Test
    void updateEmployeeNotFoundClientId() throws Exception {
        Employee employee = new Employee();
        employee.setClientId("");
        employee.setEmail("");
        EmployeeRequest employeeRequest = createEmployeeRequestWithEmptyFields();

        mockMvc.perform(
                put("/api/employees/current")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeeRequest))
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<String> response = readValue(result, new TypeReference<>() {
            });
            assertNotNull(response.getErrors());
        });
    }

    private EmployeeRequest createEmployeeRequest() {
        EmployeeRequest employeeRequest = new EmployeeRequest();
        employeeRequest.setFullName("John Doe");
        employeeRequest.setPhoneNumber("123214125");
        employeeRequest.setCompany("Google");
        employeeRequest.setPosition("Software Engineer");
        employeeRequest.setGender("Male");
        employeeRequest.setStreet("123 Street");
        employeeRequest.setCity("New York");
        employeeRequest.setProvince("New York");
        employeeRequest.setCountry("USA");
        employeeRequest.setPostalCode("12345");
        return employeeRequest;
    }

    private EmployeeRequest createEmployeeRequestWithEmptyFields() {
        EmployeeRequest employeeRequest = new EmployeeRequest();
        employeeRequest.setFullName("");
        employeeRequest.setPhoneNumber("");
        employeeRequest.setCompany("");
        employeeRequest.setPosition("");
        employeeRequest.setGender("");
        employeeRequest.setStreet("");
        employeeRequest.setCity("");
        employeeRequest.setProvince("");
        employeeRequest.setCountry("");
        employeeRequest.setPostalCode("");
        return employeeRequest;
    }

    private Employee createSampleEmployee() {
        Employee employee = new Employee();
        employee.setClientId("239414077758111751");
        employee.setEmail("user@i2dev.com");
        employee.setFullName("John Doe");
        employee.setPhoneNumber("123214125");
        employee.setCompany("Google");
        employee.setPosition("Software Engineer");
        employee.setGender("Male");

        Address address = new Address();
        address.setAddressId(UUID.randomUUID().toString());
        address.setStreet("123 Street");
        address.setCity("New York");
        address.setProvince("New York");
        address.setCountry("USA");
        address.setPostalCode("12345");

        employee.setAddress(address);
        address.setEmployee(employee);

        employeeRepository.save(employee);
        return employee;
    }

    private <T> T readValue(MvcResult result, TypeReference<T> valueTypeRef) throws IOException {
        return objectMapper.readValue(result.getResponse().getContentAsString(), valueTypeRef);
    }

    private void assertEmployeeResponseEquals(Employee expected, EmployeeResponse actual) {
        assertEquals(expected.getClientId(), actual.getClientId());
        assertEquals(expected.getEmail(), actual.getEmail());
        assertEquals(expected.getFullName(), actual.getFullName());
        assertEquals(expected.getPhoneNumber(), actual.getPhoneNumber());
        assertEquals(expected.getCompany(), actual.getCompany());
        assertEquals(expected.getPosition(), actual.getPosition());
        assertEquals(expected.getGender(), actual.getGender());
        assertAddressEquals(expected.getAddress(), actual.getAddress());
    }

    private void assertAddressEquals(Address expected, AddressResponse actual) {
        assertEquals(expected.getAddressId(), actual.getAddressId());
        assertEquals(expected.getStreet(), actual.getStreet());
        assertEquals(expected.getCity(), actual.getCity());
        assertEquals(expected.getProvince(), actual.getProvince());
        assertEquals(expected.getCountry(), actual.getCountry());
        assertEquals(expected.getPostalCode(), actual.getPostalCode());
    }
}
