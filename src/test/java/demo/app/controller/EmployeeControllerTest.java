package demo.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import demo.app.model.EmployeeRequest;
import demo.app.model.EmployeeResponse;
import demo.app.model.WebResponse;
import demo.app.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeController.class)
@AutoConfigureMockMvc(addFilters = false)
public class EmployeeControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private EmployeeService employeeService;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testRegisterEmployeeWhenNewEmployeeThenSuccess() throws Exception {
        EmployeeRequest request = new EmployeeRequest();
        doNothing().when(employeeService).register(any(EmployeeRequest.class), any());

        mockMvc.perform(post("/api/employees/register")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(new WebResponse<>("Data has been stored in the database", null))))
                .andExpect(jsonPath("$-.data").value("Data has been stored in the database"));

        verify(employeeService, times(1)).register(any(EmployeeRequest.class), any());
        verify(employeeService, times(1)).register(eq(request), any());
    }

    @Test
    void testRegisterEmployeeWhenFullNameBlankThenBadRequest() throws Exception {
        EmployeeRequest request = new EmployeeRequest();
        request.setFullName("");

        doThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Full name cannot be blank")).when(employeeService).register(eq(request), any());

        mockMvc.perform(post("/api/employees/register")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(new WebResponse<>(null, "Full name cannot be blank"))))
                .andExpect(jsonPath("$.errors").value("Full name cannot be blank"));

        verify(employeeService, times(1)).register(eq(request), any());
        verify(employeeService, times(1)).register(any(EmployeeRequest.class), any());
    }

    @Test
    void testGetCurrentEmployeeWhenCurrentEmployeeThenSuccess() throws Exception {
        EmployeeResponse response = new EmployeeResponse();
        given(employeeService.getCurrent(any())).willReturn(response);

        mockMvc.perform(get("/api/employees/current")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(new WebResponse<>(response, null))));

        verify(employeeService, times(1)).getCurrent(any());
        verify(employeeService, times(1)).getCurrent(eq(null));
    }

    @Test
    void testGetCurrentEmployeeWhenNoCurrentEmployeeThenNotFound() throws Exception {
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Current employee not found")).when(employeeService).getCurrent(any());

        mockMvc.perform(get("/api/employees/current")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(new WebResponse<>(null, "Current employee not found"))))
                .andExpect(jsonPath("$.errors").value("Current employee not found"));

        verify(employeeService, times(1)).getCurrent(any());
        verify(employeeService, times(1)).getCurrent(eq(null));
    }

    @Test
    void testUpdateCurrentEmployeeWhenCurrentEmployeeThenSuccess() throws Exception {
        EmployeeRequest request = new EmployeeRequest();
        EmployeeResponse response = new EmployeeResponse();
        given(employeeService.update(any(EmployeeRequest.class), any())).willReturn(response);

        mockMvc.perform(put("/api/employees/current")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(new WebResponse<>(response, null))));

        verify(employeeService, times(1)).update(any(EmployeeRequest.class), any());
        verify(employeeService, times(1)).update(eq(request), any());
    }

    @Test
    void testUpdateCurrentEmployeeWhenNoCurrentEmployeeThenNotFound() throws Exception {
        EmployeeRequest request = new EmployeeRequest();
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Current employee not found")).when(employeeService).update(any(EmployeeRequest.class), any());

        mockMvc.perform(put("/api/employees/current")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(new WebResponse<>(null, "Current employee not found"))))
                .andExpect(jsonPath("$.errors").value("Current employee not found"));

        verify(employeeService, times(1)).update(any(EmployeeRequest.class), any());
        verify(employeeService, times(1)).update(eq(request), any());
    }

    @Test
    void testUpdateCurrentEmployeeWhenNullRequestThenBadRequest() throws Exception {
        EmployeeRequest request = new EmployeeRequest();
        doThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request cannot be null")).when(employeeService).update(any(EmployeeRequest.class), any());

        mockMvc.perform(put("/api/employees/current")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(new WebResponse<>(null, "Request cannot be null"))))
                .andExpect(jsonPath("$.errors").value("Request cannot be null"));

        verify(employeeService, times(1)).update(any(EmployeeRequest.class), any());
        verify(employeeService, times(1)).update(eq(request), any());
    }

    @Test
    void testRemoveCurrentEmployeeWhenCurrentEmployeeThenSuccess() throws Exception {
        doNothing().when(employeeService).removeCurrent(any());

        mockMvc.perform(delete("/api/employees/current")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(new WebResponse<>("Data has been removed from the database", null))))
                .andExpect(jsonPath("$.data").value("Data has been removed from the database"));

        verify(employeeService, times(1)).removeCurrent(any());
        verify(employeeService, times(1)).removeCurrent(eq(null));
    }

    @Test
    void testRemoveCurrentEmployeeWhenNoCurrentEmployeeThenNotFound() throws Exception {
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Current employee not found")).when(employeeService).removeCurrent(any());

        mockMvc.perform(delete("/api/employees/current")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(new WebResponse<>(null, "Current employee not found"))))
                .andExpect(jsonPath("$.errors").value("Current employee not found"));

        verify(employeeService, times(1)).removeCurrent(any());
        verify(employeeService, times(1)).removeCurrent(eq(null));
    }
}