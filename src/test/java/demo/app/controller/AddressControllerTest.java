package demo.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import demo.app.model.AddressRequest;
import demo.app.model.AddressResponse;
import demo.app.model.WebResponse;
import demo.app.service.AddressService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AddressController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AddressControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private AddressService addressService;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testUpdateValidAddressRequestShouldReturnAddressResponse() throws Exception {
        AddressRequest request = new AddressRequest();
        request.setStreet("Test");
        request.setCity("Test");
        request.setProvince("Test");
        request.setCountry("Test");
        request.setPostalCode("Test");

        AddressResponse response = new AddressResponse();
        response.setAddressId("1");
        response.setStreet("Test");
        response.setCity("Test");
        response.setProvince("Test");
        response.setCountry("Test");
        response.setPostalCode("Test");

        given(addressService.updateAddress(any(AddressRequest.class), any())).willReturn(response);

        mockMvc.perform(put("/api/address/current")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(new WebResponse<>(response, null))))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.addressId").value("1"))
                .andExpect(jsonPath("$.data.street").value("Test"))
                .andExpect(jsonPath("$.data.city").value("Test"))
                .andExpect(jsonPath("$.data.province").value("Test"))
                .andExpect(jsonPath("$.data.country").value("Test"))
                .andExpect(jsonPath("$.data.postalCode").value("Test"));

        verify(addressService, times(1)).updateAddress(any(AddressRequest.class), any());
    }
}