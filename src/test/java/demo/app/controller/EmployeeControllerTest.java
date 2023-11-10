package demo.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import demo.app.model.EmployeeRequest;
import demo.app.model.EmployeeResponse;
import demo.app.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmployeeController.class)
@AutoConfigureMockMvc(addFilters = false)
public class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeService employeeService;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        objectMapper = new ObjectMapper();
    }

    @Test
    @WithMockUser
    public void testRegisterEmployeeWhenEmployeeIsRegisteredThenSuccess() throws Exception {
        EmployeeRequest request = new EmployeeRequest();
        doNothing().when(employeeService).register(any(EmployeeRequest.class), any());

        mockMvc.perform(post("/api/employees/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().json("{\"data\":\"Data has been stored in the database\"}"));
    }

    @Test
    @WithMockUser
    public void testGetCurrentEmployeeWhenEmployeeIsRetrievedThenSuccess() throws Exception {
        EmployeeResponse response = new EmployeeResponse();
        when(employeeService.getCurrent(any())).thenReturn(response);

        mockMvc.perform(get("/api/employees/current")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"data\":{}}"));
    }

    @Test
    @WithMockUser
    public void testUpdateCurrentEmployeeWhenEmployeeIsUpdatedThenSuccess() throws Exception {
        EmployeeRequest request = new EmployeeRequest();
        EmployeeResponse response = new EmployeeResponse();
        when(employeeService.update(any(EmployeeRequest.class), any())).thenReturn(response);

        mockMvc.perform(put("/api/employees/current")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"data\":{}}"));
    }

    @Test
    @WithMockUser
    public void testRemoveCurrentEmployeeWhenEmployeeIsRemovedThenSuccess() throws Exception {
        doNothing().when(employeeService).removeCurrent(any());

        mockMvc.perform(delete("/api/employees/current")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"data\":\"Data has been removed from the database\"}"));
    }
}
