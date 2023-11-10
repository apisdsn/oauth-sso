package demo.app.controller;

import demo.app.model.EmployeeResponse;
import demo.app.model.ReimbursementRequest;
import demo.app.model.ReimbursementResponse;
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

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReimbursementService reimbursementService;

    @MockBean
    private EmployeeService employeeService;

    @MockBean
    private OAuth2AuthenticatedPrincipal principal;

    @Test
    public void testUpdateReimbursementByAdminWhenCalledWithValidParametersThenReturnReimbursementResponse() throws Exception {
        ReimbursementResponse mockResponse = new ReimbursementResponse();
        when(reimbursementService.updateReimbursementByAdmin(anyLong(), any(ReimbursementRequest.class), any(OAuth2AuthenticatedPrincipal.class))).thenReturn(mockResponse);

        mockMvc.perform(patch("/api/admin/reimbursements/{reimbursementId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists());
    }


    @Test
    public void testGetReimbursementsWithFalseStatusWhenCalledThenReturnListOfReimbursementResponse() throws Exception {
        when(reimbursementService.getReimbursementsWithStatusFalse()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/admin/reimbursements/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    public void testRemoveReimbursementByAdminWhenCalledWithValidParametersThenReturnOk() throws Exception {
        mockMvc.perform(delete("/api/admin/reimbursements/{clientId}/{reimbursementId}", "clientId", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("OK"));
    }

    @Test
    public void testGetEmployeeByClientIdWhenCalledWithValidClientIdThenReturnEmployeeResponse() throws Exception {
        EmployeeResponse mockResponse = new EmployeeResponse();
        when(employeeService.getByClientId(any())).thenReturn(mockResponse);

        mockMvc.perform(get("/api/admin/employees/{clientId}", "clientId"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    public void testGetAllEmployeesWhenCalledThenReturnListOfEmployeeResponse() throws Exception {
        when(employeeService.findAllEmployee()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/admin/employees/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    public void testRemoveEmployeeWhenCalledWithValidClientIdThenReturnOk() throws Exception {
        mockMvc.perform(delete("/api/admin/employees/{clientId}", "clientId"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("Data with clientId has been removed"));
    }
}
