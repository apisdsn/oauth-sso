package demo.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import demo.app.model.AddressRequest;
import demo.app.model.AddressResponse;
import demo.app.model.WebResponse;
import demo.app.service.AddressService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
    @Disabled
    void testUpdateValidAddressRequestShouldReturnAddressResponse() throws Exception {
        AddressRequest addressRequest = new AddressRequest();
        addressRequest.setStreet("123 Main St");
        addressRequest.setCity("City");
        addressRequest.setProvince("Province");
        addressRequest.setCountry("Country");
        addressRequest.setPostalCode("12345");

        AddressResponse addressResponse = new AddressResponse();
        addressResponse.setAddressId("1");
        addressResponse.setStreet("123 Main St");
        addressResponse.setCity("City");
        addressResponse.setProvince("Province");
        addressResponse.setCountry("Country");
        addressResponse.setPostalCode("12345");
        given(addressService.updateAddress(any(AddressRequest.class), any(OAuth2AuthenticatedPrincipal.class))).willReturn(addressResponse);

        mockMvc.perform(put("/api/address/current")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addressRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(new WebResponse<>(addressResponse, null))))
                .andReturn();

        verify(addressService, times(1)).updateAddress(addressRequest, any(OAuth2AuthenticatedPrincipal.class));
    }
}