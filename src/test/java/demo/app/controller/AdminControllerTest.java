//package demo.app.controller;
//
//import demo.app.model.EmployeeResponse;
//import demo.app.service.EmployeeService;
//import demo.app.service.ReimbursementService;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
//
//import static org.mockito.Mockito.*;
//
//@WebMvcTest(AdminController.class)
//public class AdminControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private ReimbursementService reimbursementService;
//
//    @MockBean
//    private EmployeeService employeeService;
//
//    @Test
//    public void testCreateEmployeeWhenValidRequestThenReturnCreated() throws Exception {
//        EmployeeResponse employeeResponse = new EmployeeResponse();
//        when(employeeService.createEmployee(any())).thenReturn(employeeResponse);
//
//        mockMvc.perform(MockMvcRequestBuilders.post("/api/admin/employees")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("{}"))
//                .andExpect(MockMvcResultMatchers.status().isCreated());
//
//        verify(employeeService, times(1)).createEmployee(any());
//    }
//
//    @Test
//    public void testCreateEmployeeWhenExceptionThenReturnInternalServerError() throws Exception {
//        when(employeeService.createEmployee(any())).thenThrow(new RuntimeException());
//
//        mockMvc.perform(MockMvcRequestBuilders.post("/api/admin/employees")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("{}"))
//                .andExpect(MockMvcResultMatchers.status().isInternalServerError());
//
//        verify(employeeService, times(1)).createEmployee(any());
//    }
//
//    @Test
//    public void testUpdateEmployeeWhenValidRequestThenReturnOk() throws Exception {
//        EmployeeResponse employeeResponse = new EmployeeResponse();
//        when(employeeService.updateEmployee(any(), any())).thenReturn(employeeResponse);
//
//        mockMvc.perform(MockMvcRequestBuilders.put("/api/admin/employees/{clientId}", "testClientId")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("{}"))
//                .andExpect(MockMvcResultMatchers.status().isOk());
//
//        verify(employeeService, times(1)).updateEmployee(any(), any());
//    }
//
//    @Test
//    public void testUpdateEmployeeWhenExceptionThenReturnInternalServerError() throws Exception {
//        when(employeeService.updateEmployee(any(), any())).thenThrow(new RuntimeException());
//
//        mockMvc.perform(MockMvcRequestBuilders.put("/api/admin/employees/{clientId}", "testClientId")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("{}"))
//                .andExpect(MockMvcResultMatchers.status().isInternalServerError());
//
//        verify(employeeService, times(1)).updateEmployee(any(), any());
//    }
//
//    @Test
//    public void testDeleteEmployeeWhenValidRequestThenReturnOk() throws Exception {
//        doNothing().when(employeeService).deleteEmployee(any());
//
//        mockMvc.perform(MockMvcRequestBuilders.delete("/api/admin/employees/{clientId}", "testClientId"))
//                .andExpect(MockMvcResultMatchers.status().isOk());
//
//        verify(employeeService, times(1)).deleteEmployee(any());
//    }
//
//    @Test
//    public void testDeleteEmployeeWhenExceptionThenReturnInternalServerError() throws Exception {
//        doThrow(new RuntimeException()).when(employeeService).deleteEmployee(any());
//
//        mockMvc.perform(MockMvcRequestBuilders.delete("/api/admin/employees/{clientId}", "testClientId"))
//                .andExpect(MockMvcResultMatchers.status().isInternalServerError());
//
//        verify(employeeService, times(1)).deleteEmployee(any());
//    }
//}
