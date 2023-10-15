package demo.app.controller;

import demo.app.model.AddressRequest;
import demo.app.model.AddressResponse;
import demo.app.model.WebResponse;
import demo.app.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/addresses")
public class AddressController {

    @Autowired
    private AddressService addressService;

    @PreAuthorize("hasAuthority('user')")
    @PatchMapping(path = "/current", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<AddressResponse> update(@RequestBody AddressRequest request, @AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal, Authentication auth) {
        validateAuthentication(auth);
        AddressResponse addressResponse = addressService.updateAddress(request, principal);
        return WebResponse.<AddressResponse>builder().data(addressResponse).build();
    }

    @PreAuthorize("hasAuthority('admin') or hasAuthority('manager')")
    @PatchMapping(path = "/{clientId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<AddressResponse> updateAddressByClientId(@RequestBody AddressRequest request, @PathVariable("clientId") String clientId, @AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal, Authentication auth) {
        validateAuthentication(auth);
        AddressResponse addressResponse = addressService.updateAddressByClientId(request, clientId, principal);
        return WebResponse.<AddressResponse>builder().data(addressResponse).build();
    }

    private void validateAuthentication(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not authenticated");
        }
    }
}

