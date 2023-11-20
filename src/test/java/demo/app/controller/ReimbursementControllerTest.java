//package demo.app.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import demo.app.model.ReimbursementRequest;
//import demo.app.model.ReimbursementResponse;
//import demo.app.service.ReimbursementService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.math.BigDecimal;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@WebMvcTest(ReimbursementController.class)
//@AutoConfigureMockMvc(addFilters = false)
//public class ReimbursementControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private ReimbursementService reimbursementService;
//
//    @MockBean
//    private OAuth2AuthenticatedPrincipal principal;
//
//    private ObjectMapper objectMapper;
//
//    @BeforeEach
//    public void setup() {
//        objectMapper = new ObjectMapper();
//    }
//
//    @Test
//    public void testCreateReimbursementWhenValidRequestThenReturnCreated() throws Exception {
//        ReimbursementRequest request = new ReimbursementRequest();
//        request.setAmount(new BigDecimal(1000));
//        request.setActivity("Travel");
//        request.setTypeReimbursement("Transport");
//        request.setDescription("Travel to client");
//
//        ReimbursementResponse response = new ReimbursementResponse();
//        response.setAmount("Rp1.000,00");
//        response.setActivity("Travel");
//        response.setTypeReimbursement("Transport");
//        response.setDescription("Travel to client");
//
//        Mockito.when(reimbursementService.create(Mockito.any(ReimbursementRequest.class), Mockito.any(OAuth2AuthenticatedPrincipal.class)))
//                .thenReturn(response);
//
//        mockMvc.perform(post("/api/reimbursements")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.data.amount").value("Rp1.000,00"))
//                .andExpect(jsonPath("$.data.activity").value("Travel"))
//                .andExpect(jsonPath("$.data.typeReimbursement").value("Transport"))
//                .andExpect(jsonPath("$.data.description").value("Travel to client"))
//                .andReturn();
//
//        Mockito.verify(reimbursementService, Mockito.times(1)).create(Mockito.any(ReimbursementRequest.class), Mockito.any(OAuth2AuthenticatedPrincipal.class));
//    }
//
//    @Test
//    public void testUpdateReimbursementWhenValidRequestThenReturnOk() throws Exception {
//        long reimbursementId = 1L;
//        ReimbursementRequest request = new ReimbursementRequest();
//        request.setAmount(new BigDecimal(2000));
//        request.setActivity("Meal");
//        request.setTypeReimbursement("Food");
//        request.setDescription("Lunch with client");
//
//        ReimbursementResponse response = new ReimbursementResponse();
//        response.setAmount("Rp2.000,00");
//        response.setActivity("Meal");
//        response.setTypeReimbursement("Food");
//        response.setDescription("Lunch with client");
//
//        Mockito.when(reimbursementService.updateReimbursementUser(Mockito.anyLong(), Mockito.any(ReimbursementRequest.class), Mockito.any(OAuth2AuthenticatedPrincipal.class)))
//                .thenReturn(response);
//
//        mockMvc.perform(patch("/api/reimbursements/" + reimbursementId)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.data.amount").value("Rp2.000,00"))
//                .andExpect(jsonPath("$.data.activity").value("Meal"))
//                .andExpect(jsonPath("$.data.typeReimbursement").value("Food"))
//                .andExpect(jsonPath("$.data.description").value("Lunch with client"));
//
//        Mockito.verify(reimbursementService, Mockito.times(1)).updateReimbursementUser(Mockito.anyLong(), Mockito.any(ReimbursementRequest.class), Mockito.any(OAuth2AuthenticatedPrincipal.class));
//    }
//
//    @Test
//    public void testDeleteReimbursementWhenValidRequestThenReturnOk() throws Exception {
//        Long reimbursementId = 1L;
//
//        mockMvc.perform(delete("/api/reimbursements/" + reimbursementId))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.data").value("Reimbursement deleted"));
//
//        Mockito.verify(reimbursementService, Mockito.times(1)).removeReimbursementByUser(Mockito.anyLong(), Mockito.any(OAuth2AuthenticatedPrincipal.class));
//    }
//}
