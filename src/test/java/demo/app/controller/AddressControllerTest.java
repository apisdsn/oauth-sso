//package demo.app.controller;
//
//
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import demo.app.entity.Address;
//import demo.app.entity.Employee;
//import demo.app.model.AddressRequest;
//import demo.app.model.AddressResponse;
//import demo.app.model.WebResponse;
//import demo.app.repository.AddressRepository;
//import demo.app.repository.EmployeeRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.MvcResult;
//
//import java.io.IOException;
//import java.util.UUID;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//public class AddressControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private AddressRepository addressRepository;
//
//    @Autowired
//    private EmployeeRepository employeeRepository;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//
//    private String token;
//
//    @BeforeEach
//    void setUp() {
//        employeeRepository.deleteAll();
//        addressRepository.deleteAll();
//        token = "ufs6Oiqp1-JyhvP8sFM8_7c2gq0F_3cysAKGGCzPoNqahGs4ZUuD1dBrneCYgfjdhE0Faw8";
//    }
//
//
//    @Test
//    void updateAddressSuccessWithToken() throws Exception {
//        createSampleEmployee();
//        AddressRequest addressRequest = new AddressRequest();
//        addressRequest.setStreet("Jl Kenangan");
//        addressRequest.setCity("Bekasi");
//        addressRequest.setProvince("Jawa Barat");
//        addressRequest.setCountry("Indonesia");
//        addressRequest.setPostalCode("1232");
//
//        mockMvc.perform(put("/api/address/current")
//                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
//                .accept(MediaType.APPLICATION_JSON)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(addressRequest))
//        ).andExpectAll(status().isOk()).andDo(result -> {
//            WebResponse<AddressResponse> response = readValue(result, new TypeReference<>() {
//            });
//            assertNull(response.getErrors());
//            assertEquals(addressRequest.getStreet(), response.getData().getStreet());
//            assertEquals(addressRequest.getCity(), response.getData().getCity());
//            assertEquals(addressRequest.getProvince(), response.getData().getProvince());
//            assertEquals(addressRequest.getCountry(), response.getData().getCountry());
//            assertEquals(addressRequest.getPostalCode(), response.getData().getPostalCode());
//        });
//    }
//
//    @Test
//    void updateAddressUnauthorizedWithTokenNull() throws Exception {
//        createSampleEmployee();
//        AddressRequest addressRequest = new AddressRequest();
//        addressRequest.setStreet("Jl Kenangan");
//
//
//        mockMvc.perform(put("/api/address/current")
//                .header(HttpHeaders.AUTHORIZATION, "Bearer " + null)
//                .accept(MediaType.APPLICATION_JSON)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(addressRequest))
//        ).andExpect(status().isUnauthorized());
//    }
//
//    @Test
//    void updateAddressNotFoundClientId() throws Exception {
//        Employee employee = new Employee();
//        employee.setClientId("");
//        employee.setEmail("");
//
//        AddressRequest addressRequest = new AddressRequest();
//        addressRequest.setStreet("Jl Kenangan");
//        addressRequest.setCity("Bekasi");
//        addressRequest.setProvince("Jawa Barat");
//        addressRequest.setCountry("Indonesia");
//        addressRequest.setPostalCode("1232");
//
//        mockMvc.perform(put("/api/address/current")
//                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
//                .accept(MediaType.APPLICATION_JSON)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(addressRequest))
//        ).andExpectAll(status().isBadRequest()).andDo(result -> {
//            WebResponse<AddressResponse> response = readValue(result, new TypeReference<>() {
//            });
//            assertNotNull(response.getErrors());
//        });
//    }
//
//    @Test
//    void updateAddressBadRequest() throws Exception {
//        AddressRequest addressRequest = new AddressRequest();
//        addressRequest.setCity("");
//        addressRequest.setProvince("");
//
//        mockMvc.perform(put("/api/address/current")
//                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
//                .accept(MediaType.APPLICATION_JSON)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(addressRequest))
//        ).andExpectAll(status().isBadRequest()).andDo(result -> {
//            WebResponse<AddressResponse> response = readValue(result, new TypeReference<>() {
//            });
//            assertNotNull(response.getErrors());
//        });
//    }
//
//    private <T> T readValue(MvcResult result, TypeReference<T> valueTypeRef) throws IOException {
//        return objectMapper.readValue(result.getResponse().getContentAsString(), valueTypeRef);
//    }
//
//    private void createSampleEmployee() {
//        Employee employee = new Employee();
//        employee.setClientId("239414077758111751");
////        employee.setClientId("239484551712276487@i2dev");
//        employee.setEmail("user@i2dev.com");
//        employee.setFullName("John Doe");
//        employee.setPhoneNumber("123214125");
//        employee.setCompany("Google");
//        employee.setPosition("Software Engineer");
//        employee.setGender("Male");
//
//        Address address = new Address();
//        address.setAddressId(UUID.randomUUID().toString());
//        address.setStreet("123 Street");
//        address.setCity("New York");
//        address.setProvince("New York");
//        address.setCountry("USA");
//        address.setPostalCode("12345");
//
//        employee.setAddress(address);
//        address.setEmployee(employee);
//
//        employeeRepository.save(employee);
//    }
//}
