//package demo.app.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
//import demo.app.model.ReimbursementRequest;
//import demo.app.model.ReimbursementResponse;
//import demo.app.model.WebResponse;
//import demo.app.service.ReimbursementService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.HashMap;
//import java.util.Map;
//
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@WebMvcTest(ReimbursementController.class)
//@AutoConfigureMockMvc(addFilters = false)
//public class ReimbursementControllerTest {
//
//    private final ObjectMapper objectMapper = new ObjectMapper();
//    @Autowired
//    private MockMvc mockMvc;
//    @MockBean
//    private ReimbursementService reimbursementService;
//    private OAuth2AuthenticatedPrincipal principal;
//
//    @BeforeEach
//    public void setup() {
//        objectMapper.registerModule(new JavaTimeModule());
//        Map<String, Object> attributes = new HashMap<>();
//        attributes.put("sub", "123");
//        Authentication authentication = Mockito.mock(Authentication.class);
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//        principal = Mockito.mock(OAuth2AuthenticatedPrincipal.class);
//        Mockito.when(principal.getAttributes()).thenReturn(attributes);
//    }
//
//    @Test
//    public void testCreateReimbursementWhenValidRequestThenReturnCreated() throws Exception {
//        ReimbursementRequest request = new ReimbursementRequest();
//        request.setAmount(new BigDecimal(1000));
//        request.setActivity("Travel");
//        request.setTypeReimbursement("Transport");
//        request.setDescription("Travel to client location");
//
//        ReimbursementResponse response = new ReimbursementResponse();
//        response.setReimbursementId(1L);
//        response.setEmployeeId(1L);
//        response.setAmount("Rp1,000.00");
//        response.setActivity("Travel");
//        response.setTypeReimbursement("Transport");
//        response.setDescription("Travel to client location");
//        response.setStatus(false);
//        response.setDateCreated(LocalDateTime.now());
//
//        Mockito.when(reimbursementService.create(Mockito.any(ReimbursementRequest.class), Mockito.any(OAuth2AuthenticatedPrincipal.class))).thenReturn(response);
//
//        mockMvc.perform(MockMvcRequestBuilders.post("/api/reimbursements")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isCreated())
//                .andExpect(content().json(objectMapper.writeValueAsString(WebResponse.<ReimbursementResponse>builder().data(response).build())));
//    }
//
//    @Test
//    public void testUpdateReimbursementWhenValidRequestThenReturnOk() throws Exception {
//        Long reimbursementId = 1L;
//        ReimbursementRequest request = new ReimbursementRequest();
//        request.setAmount(new BigDecimal(2000));
//        request.setActivity("Training");
//        request.setTypeReimbursement("Education");
//        request.setDescription("Training expenses");
//
//        ReimbursementResponse response = new ReimbursementResponse();
//        response.setReimbursementId(reimbursementId);
//        response.setEmployeeId(1L);
//        response.setAmount("Rp2,000.00");
//        response.setActivity("Training");
//        response.setTypeReimbursement("Education");
//        response.setDescription("Training expenses");
//        response.setStatus(false);
//        response.setDateCreated(LocalDateTime.now());
//
//        Mockito.when(reimbursementService.updateReimbursementUser(Mockito.anyLong(), Mockito.any(ReimbursementRequest.class), Mockito.any(OAuth2AuthenticatedPrincipal.class))).thenReturn(response);
//
//        mockMvc.perform(MockMvcRequestBuilders.patch("/api/reimbursements/" + reimbursementId)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//
//                .andExpect(status().isOk())
//                .andExpect(content().json(objectMapper.writeValueAsString(WebResponse.<ReimbursementResponse>builder().data(response).build())));
//    }
//
//    @Test
//    public void testDeleteReimbursementWhenValidIdThenReturnOk() throws Exception {
//        Long reimbursementId = 1L;
//
//        mockMvc.perform(MockMvcRequestBuilders.delete("/api/reimbursements/" + reimbursementId))
//
//                .andExpect(status().isOk())
//                .andExpect(content().json(objectMapper.writeValueAsString(WebResponse.<String>builder().data("OK").build())));
//
//        Mockito.verify(reimbursementService, Mockito.times(1)).removeReimbursementByUser(reimbursementId, principal);
//    }
//}
