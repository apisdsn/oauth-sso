package demo.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import demo.app.entity.Reimbursement;
import demo.app.model.ReimbursementRequest;
import demo.app.model.ReimbursementResponse;
import demo.app.model.WebResponse;
import demo.app.service.ReimbursementService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(ReimbursementController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ReimbursementControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ReimbursementService reimbursementService;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateReimbursementWhenValidRequestThenReturnCreated() throws Exception {
        ReimbursementRequest request = new ReimbursementRequest();
        request.setAmount(new BigDecimal(1000));
        request.setActivity("Travel");
        request.setTypeReimbursement("Transport");
        request.setDescription("Travel to client");
        ReimbursementResponse response = new ReimbursementResponse();
        response.setReimbursementId(1L);
        response.setAmount("Rp1.000,00");
        response.setActivity("Travel");
        response.setTypeReimbursement("Transport");
        response.setDescription("Travel to client");
        given(reimbursementService.create(any(ReimbursementRequest.class), any())).willReturn(response);

        mockMvc.perform(post("/api/reimbursements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(new WebResponse<>(response, null))))
                .andExpect(jsonPath("data.reimbursementId").value(1L))
                .andExpect(jsonPath("data.amount").value("Rp1.000,00"))
                .andExpect(jsonPath("data.activity").value("Travel"))
                .andExpect(jsonPath("data.typeReimbursement").value("Transport"))
                .andExpect(jsonPath("data.description").value("Travel to client"));

        verify(reimbursementService, times(1)).create(any(ReimbursementRequest.class), any());
    }

    @Test
    void testUpdateReimbursementWhenValidRequestThenReturnOk() throws Exception {
        Reimbursement reimbursement = new Reimbursement();
        reimbursement.setReimbursementId(1L);
        reimbursement.setAmount(BigDecimal.valueOf(1000.00));
        reimbursement.setDescription("Expense reimbursement");
        reimbursement.setActivity("Travel");
        reimbursement.setTypeReimbursement("Transport");

        ReimbursementRequest request = new ReimbursementRequest();
        request.setAmount(new BigDecimal(2000));
        request.setActivity("Meal");
        request.setTypeReimbursement("Food");
        request.setDescription("Lunch with client");

        ReimbursementResponse response = new ReimbursementResponse();
        response.setAmount("Rp2.000,00");
        response.setActivity("Meal");
        response.setTypeReimbursement("Food");
        response.setDescription("Lunch with client");

        given(reimbursementService.updateReimbursementUser(anyLong(), any(ReimbursementRequest.class), any())).willReturn(response);

        mockMvc.perform(patch("/api/reimbursements/" + 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(new WebResponse<>(response, null))))
                .andExpect(jsonPath("$.data.amount").value("Rp2.000,00"))
                .andExpect(jsonPath("$.data.activity").value("Meal"))
                .andExpect(jsonPath("$.data.typeReimbursement").value("Food"))
                .andExpect(jsonPath("$.data.description").value("Lunch with client"));

        verify(reimbursementService, times(1)).updateReimbursementUser(anyLong(), any(ReimbursementRequest.class), any());
    }

    @Test
    void testDeleteReimbursementWhenValidRequestThenReturnOk() throws Exception {
        doNothing().when(reimbursementService).removeReimbursementByUser(anyLong(), any());

        mockMvc.perform(delete("/api/reimbursements/" + 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").value("Reimbursement deleted"));
    }
}
