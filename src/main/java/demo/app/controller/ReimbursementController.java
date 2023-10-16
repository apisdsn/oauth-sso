package demo.app.controller;

import demo.app.model.ReimbursementRequest;
import demo.app.model.ReimbursementResponse;
import demo.app.model.WebResponse;
import demo.app.service.ReimbursementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/reimbursement")
public class ReimbursementController {

    @Autowired
    private ReimbursementService reimbursementService;

    @PostMapping(path = "/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public WebResponse<ReimbursementResponse> create(@RequestBody ReimbursementRequest request, @AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal, Authentication auth) {
        validateAuthentication(auth);
        ReimbursementResponse reimbursementResponse = reimbursementService.createOrUpdateReimbursement(request, principal, auth, null, false);
        return WebResponse.<ReimbursementResponse>builder().data(reimbursementResponse).build();
    }

    @PatchMapping(path = "/current", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<ReimbursementResponse> updateUser(@RequestBody ReimbursementRequest request, @AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal, Authentication auth) {
        validateAuthentication(auth);
        ReimbursementResponse reimbursementResponse = reimbursementService.createOrUpdateReimbursement(request, principal, auth, null, false);
        return WebResponse.<ReimbursementResponse>builder().data(reimbursementResponse).build();
    }

    @PatchMapping(path = "/update/admin/{clientId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<ReimbursementResponse> updateAdmin(@RequestBody ReimbursementRequest request, @PathVariable("clientId") String clientId, @AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal, Authentication auth) {
        validateAuthentication(auth);
        ReimbursementResponse reimbursementResponse = reimbursementService.createOrUpdateReimbursement(request, principal, auth, clientId, true);
        return WebResponse.<ReimbursementResponse>builder().data(reimbursementResponse).build();
    }

    @DeleteMapping("/delete/user")
    public WebResponse<ReimbursementResponse> deleteReimbursementByUser(@AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal, Authentication auth) {
        validateAuthentication(auth);
        ReimbursementResponse reimbursementResponse = reimbursementService.deleteReimbursementByUser(principal, auth);
        return WebResponse.<ReimbursementResponse>builder().data(reimbursementResponse).build();
    }

    // admin or manager service
    @DeleteMapping("/delete/admin/{clientId}")
    public WebResponse<ReimbursementResponse> deleteReimbursementByAdmin(@PathVariable String clientId, @AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal, Authentication auth) {
        validateAuthentication(auth);
        ReimbursementResponse reimbursementResponse = reimbursementService.deleteReimbursementByAdmin(clientId, principal, auth);
        return WebResponse.<ReimbursementResponse>builder().data(reimbursementResponse).build();
    }

    // admin or manager service
    @GetMapping(path = "/status", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public WebResponse<List<ReimbursementResponse>> getReimbursementsWithStatusFalse(@AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal, Authentication auth) {
        validateAuthentication(auth);
        List<ReimbursementResponse> reimbursementResponses = reimbursementService.getReimbursementsWithStatusFalse(principal, auth);
        return WebResponse.<List<ReimbursementResponse>>builder().data(reimbursementResponses).build();
    }

    private void validateAuthentication(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not authenticated");
        }
    }
}
