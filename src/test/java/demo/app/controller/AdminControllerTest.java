package demo.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import demo.app.model.EmployeeResponse;
import demo.app.model.ReimbursementRequest;
import demo.app.model.ReimbursementResponse;
import demo.app.model.WebResponse;
import demo.app.service.EmployeeService;
import demo.app.service.ReimbursementService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AdminControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReimbursementService reimbursementService;

    @MockBean
    private EmployeeService employeeService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testUpdateReimbursementByAdminWhenCalledWithValidParametersThenReturnReimbursementResponse() throws Exception {
        ReimbursementResponse response = new ReimbursementResponse();
        ReimbursementRequest request = new ReimbursementRequest();
        given(reimbursementService.updateReimbursementByAdmin(any(), anyLong(), any(ReimbursementRequest.class), any(OAuth2AuthenticatedPrincipal.class))).willReturn(response);

        mockMvc.perform(patch("/api/admin/reimbursements/{clientId}/{reimbursementId}", "123", 1L)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(new WebResponse<>(null, null))));
    }

    @Test
    void testGetReimbursementsWithFalseStatusWhenCalledThenReturnListOfReimbursementResponse() throws Exception {
        ReimbursementResponse reimbursementResponse = new ReimbursementResponse();
        reimbursementResponse.setReimbursementId(1L);
        reimbursementResponse.setAmount("Rp.100000.00");
        reimbursementResponse.setStatus(false);
        reimbursementResponse.setDateCreated(LocalDateTime.now());
        reimbursementResponse.setDateUpdated(LocalDateTime.now());

        given(reimbursementService.getReimbursementsWithStatusFalse()).willReturn(Collections.singletonList(reimbursementResponse));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String expectedDateCreated = reimbursementResponse.getDateCreated().format(formatter);
        String expectedDateUpdated = reimbursementResponse.getDateUpdated().format(formatter);

        mockMvc.perform(get("/api/admin/reimbursements/status")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(new WebResponse<>(Collections.singletonList(reimbursementResponse), null))))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].reimbursementId").value(1L))
                .andExpect(jsonPath("$.data[0].amount").value("Rp.100000.00"))
                .andExpect(jsonPath("$.data[0].status").value(false))
                .andExpect(jsonPath("$.data[0].dateCreated").value(expectedDateCreated))
                .andExpect(jsonPath("$.data[0].dateUpdated").value(expectedDateUpdated))
                .andExpect(jsonPath("$.data[0]").exists());
    }

    @Test
    void testRemoveReimbursementByAdminWhenCalledWithValidParametersThenReturnOk() throws Exception {
        mockMvc.perform(delete("/api/admin/reimbursements/{clientId}/{reimbursementId}", "123", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(new WebResponse<>("OK", null))));
    }

    @Test
    void testGetEmployeeByClientIdWhenCalledWithValidClientIdThenReturnEmployeeResponse() throws Exception {
        EmployeeResponse response = new EmployeeResponse();
        response.setClientId("123");
        response.setFullName("John Doe");
        response.setEmail("john.doe@example.com");

        when(employeeService.getByClientId(any())).thenReturn(response);

        mockMvc.perform(get("/api/admin/employees/{clientId}", "123"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(new WebResponse<>(response, null))))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.clientId").value("123"))
                .andExpect(jsonPath("$.data.fullName").value("John Doe"))
                .andExpect(jsonPath("$.data.email").value("john.doe@example.com"));
    }

    @Test
    void testGetAllEmployeesWhenCalledThenReturnListOfEmployeeResponse() throws Exception {
        when(employeeService.findAllEmployee()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/admin/employees/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(new WebResponse<>(Collections.emptyList(), null))))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void testRemoveEmployeeWhenCalledWithValidClientIdThenReturnOk() throws Exception {
        mockMvc.perform(delete("/api/admin/employees/{clientId}", "123"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(new WebResponse<>("Data with clientId has been removed", null))))
                .andExpect(jsonPath("$.data").value("Data with clientId has been removed"));
    }
}
