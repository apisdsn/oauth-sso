package demo.app.controller;

import demo.app.model.ReimbursementRequest;
import demo.app.model.ReimbursementResponse;
import demo.app.model.WebResponse;
import demo.app.service.ReimbursementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ReimbursementController {

    @Autowired
    private ReimbursementService reimbursementService;

    @PostMapping(path = "/api/reimbursement/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public WebResponse<ReimbursementResponse> create(@RequestBody ReimbursementRequest request, @AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal) {
        ReimbursementResponse reimbursementResponse = reimbursementService.create(request, principal);
        return WebResponse.<ReimbursementResponse>builder().data(reimbursementResponse).build();
    }

    @PatchMapping(path = "/api/reimbursement/current/{reimbursementId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<ReimbursementResponse> updateUser(@PathVariable("reimbursementId") Long reimbursementId, @RequestBody ReimbursementRequest request, @AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal) {
        ReimbursementResponse reimbursementResponse = reimbursementService.updateReimbursementUser(reimbursementId, request, principal);
        return WebResponse.<ReimbursementResponse>builder().data(reimbursementResponse).build();
    }

    // admin or manager service
    @PatchMapping(path = "/api/admin/update/reimbursement/{reimbursementId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<ReimbursementResponse> updateAdmin(@PathVariable("reimbursementId") Long reimbursementId, @RequestBody ReimbursementRequest request, @AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal) {
        ReimbursementResponse reimbursementResponse = reimbursementService.updateReimbursementByAdmin(reimbursementId, request, principal);
        return WebResponse.<ReimbursementResponse>builder().data(reimbursementResponse).build();
    }

    @DeleteMapping("/api/admin/delete/reimbursement/{reimbursementId}")
    public WebResponse<String> deleteReimbursementByUser(@PathVariable("reimbursementId") Long reimbursementId, @AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal) {
        reimbursementService.removeReimbursementByUser(reimbursementId, principal);
        return WebResponse.<String>builder().data("OK").build();
    }

    // admin or manager service
    @GetMapping(path = "/api/admin/reimbursement/status", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public WebResponse<List<ReimbursementResponse>> getReimbursementsWithStatusFalse() {
        List<ReimbursementResponse> reimbursementResponses = reimbursementService.getReimbursementsWithStatusFalse();
        return WebResponse.<List<ReimbursementResponse>>builder().data(reimbursementResponses).build();
    }

}
