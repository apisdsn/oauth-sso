package demo.app.controller;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import demo.app.entity.Address;
import demo.app.entity.Employee;
import demo.app.entity.Reimbursement;
import demo.app.model.ReimbursementRequest;
import demo.app.model.ReimbursementResponse;
import demo.app.model.WebResponse;
import demo.app.repository.EmployeeRepository;
import demo.app.repository.ReimbursementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:reimbursement.sql")
@Transactional
public class ReimbursementControllerIntegrationTest {
    @Autowired
    private ReimbursementRepository reimbursementRepository;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    private Reimbursement reimbursement;
    private String token;

    @BeforeEach
    void setUp() {
        Employee employee = new Employee();
        employee.setClientId("239414077758111751");
        employee.setEmail("user@i2dev.com");
        employee.setFullName("John Doe");
        employee.setGender("Laki-Laki");
        employee.setCompany("PT Cinta Sejati");
        employee.setPosition("Software Engineer");

        Address address = new Address();
        address.setAddressId(UUID.randomUUID().toString());
        address.setStreet("Jl Kenangan");
        address.setCity("Tanggerang Selatan");
        address.setProvince("Jawa Barat");
        address.setCountry("Indonesia");
        address.setPostalCode("155882");
        address.setEmployee(employee);

        employee.setAddress(address);
        employeeRepository.save(employee);

        reimbursement = new Reimbursement();
        reimbursement.setReimbursementId(1L);
        reimbursement.setAmount(BigDecimal.valueOf(1000.00));
        reimbursement.setActivity("Travel");
        reimbursement.setTypeReimbursement("Transport");
        reimbursement.setDescription("Travel to client location");
        reimbursement.setStatus(false);
        reimbursement.setEmployee(employee);

        List<Reimbursement> reimbursements = new ArrayList<>();
        reimbursements.add(reimbursement);
        employee.setReimbursements(reimbursements);

        token = "tLdusVeZsA1sVHER9GbaAwVCizB-oPK1LQBcTEjUVZwuTQ_2GcDRlgRSiDUIz7JaObfZYng";

    }

    @Test
    void testCreateReimbursementWhenAllParametersAreValidThenReturnReimbursementResponse() throws Exception {
        reimbursementRepository.deleteAll();
        ReimbursementRequest reimbursementRequest = new ReimbursementRequest();
        reimbursementRequest.setAmount(BigDecimal.valueOf(1000.00));
        reimbursementRequest.setActivity("Travel");
        reimbursementRequest.setTypeReimbursement("Transport");
        reimbursementRequest.setDescription("Travel to client location");
        reimbursementRequest.setStatus(false);

        mockMvc.perform(post("/api/reimbursements")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reimbursementRequest))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)

        ).andExpect(
                status().isCreated()
        ).andDo(result -> {
            WebResponse<ReimbursementResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            DecimalFormatSymbols symbols = new DecimalFormatSymbols();
            symbols.setGroupingSeparator('.');
            symbols.setDecimalSeparator(',');

            DecimalFormat decimalFormat = new DecimalFormat("Rp#,##0.00", symbols);

            assertEquals(response.getData().getReimbursementId(), reimbursement.getReimbursementId());
            assertEquals(response.getData().getAmount(), decimalFormat.format(reimbursement.getAmount()));
            assertEquals(response.getData().getActivity(), reimbursement.getActivity());
            assertEquals(response.getData().getTypeReimbursement(), reimbursement.getTypeReimbursement());
            assertEquals(response.getData().getDescription(), reimbursement.getDescription());
            assertEquals(response.getData().getStatus(), reimbursement.getStatus());
        });
    }

    @Test
    void testCreateReimbursementWhenMissingRequiredFieldsThenBadRequest() throws Exception {
        ReimbursementRequest reimbursementRequest = new ReimbursementRequest();
        reimbursementRequest.setAmount(BigDecimal.valueOf(0));

        mockMvc.perform(post("/api/reimbursements")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reimbursementRequest))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
        ).andExpect(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertEquals("Amount cannot be zero or null", response.getErrors());
            assertNull(response.getData());
        });
    }

    @Test
    void testCreateReimbursementWhenTokenNullThenUnauthorized() throws Exception {
        ReimbursementRequest reimbursementRequest = new ReimbursementRequest();
        reimbursementRequest.setAmount(BigDecimal.valueOf(1000.00));
        reimbursementRequest.setActivity("Travel");
        reimbursementRequest.setTypeReimbursement("Transport");
        reimbursementRequest.setDescription("Travel to client location");
        reimbursementRequest.setStatus(false);

        mockMvc.perform(post("/api/reimbursements")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reimbursementRequest))
        ).andExpect(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertEquals("You are not authenticated to perform this operation", response.getErrors());
            assertNull(response.getData());
        });
    }
}
