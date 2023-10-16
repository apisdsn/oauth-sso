package demo.app.controller;

import demo.app.model.EmployeeRequest;
import demo.app.model.EmployeeResponse;
import demo.app.model.WebResponse;
import demo.app.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @PreAuthorize("hasAuthority('user')")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(path = "/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<String> register(@RequestBody EmployeeRequest request, @AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal, Authentication auth) {
        validateAuthentication(auth);
        employeeService.register(request, principal, auth);
        return WebResponse.<String>builder().data("Successes").build();
    }

    @PreAuthorize("hasAuthority('user')")
    @GetMapping(path = "/current", produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<EmployeeResponse> get(@AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal, Authentication auth) {
        validateAuthentication(auth);
        EmployeeResponse employeeResponse = employeeService.getCurrent(principal, auth);
        return WebResponse.<EmployeeResponse>builder().data(employeeResponse).build();
    }

    @PreAuthorize("hasAuthority('user')")
    @PatchMapping(path = "/current", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<EmployeeResponse> update(@RequestBody EmployeeRequest request, @AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal, Authentication auth) {
        validateAuthentication(auth);
        EmployeeResponse employeeResponse = employeeService.update(request, principal);
        return WebResponse.<EmployeeResponse>builder().data(employeeResponse).build();
    }

    @DeleteMapping(path = "/current", produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<String> remove(@AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal, Authentication auth) {
        validateAuthentication(auth);
        employeeService.removeCurrent(principal, auth);
        return WebResponse.<String>builder().data("Data has removed").build();
    }

    // admin or manager controller
//    @PreAuthorize("hasAuthority('admin') or hasAuthority('manager')")
    @GetMapping(path = "/admin/{clientId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<EmployeeResponse> findByClientId(@PathVariable("clientId") String clientId, @AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal, Authentication auth) {
        validateAuthentication(auth);
        EmployeeResponse employeeResponse = employeeService.getByClientId(clientId, principal, auth);
        log.debug("employeeResponse: {}", employeeResponse);
        return WebResponse.<EmployeeResponse>builder().data(employeeResponse).build();
    }

    // admin or manager controller
//    @PreAuthorize("hasAuthority('admin') or hasAuthority('manager')")
    @GetMapping(path = "/admin/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<List<EmployeeResponse>> getAll(@AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal, Authentication auth) {
        validateAuthentication(auth);
        List<EmployeeResponse> employeeResponseAll = employeeService.findAllEmployee(principal, auth);
        log.debug("employeeResponseAll: {}", employeeResponseAll);
        return WebResponse.<List<EmployeeResponse>>builder().data(employeeResponseAll).build();
    }

    // admin or manager controller
    @DeleteMapping(path = "/admin/{clientId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<String> remove(@PathVariable("clientId") String clientId, @AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal, Authentication auth) {
        validateAuthentication(auth);
        employeeService.removeByClientId(clientId, principal, auth);
        return WebResponse.<String>builder().data("Data with clientId has removed").build();
    }

    private void validateAuthentication(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not authenticated");
        }
    }
}
