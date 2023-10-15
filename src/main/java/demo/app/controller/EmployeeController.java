package demo.app.controller;

import demo.app.model.*;
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
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PreAuthorize("hasAuthority('user')")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(path = "/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<String> register (@RequestBody EmployeeRequest request ,@AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal, Authentication auth) {
        validateAuthentication(auth);
        employeeService.register(request, principal, auth);
        return WebResponse.<String>builder().data("Success").build();
    }
    @PreAuthorize("hasAuthority('user')")
    @GetMapping(path = "/current", produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<EmployeeResponse> get(@AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal, Authentication auth) {
        EmployeeResponse employeeResponse = employeeService.getCurrent(principal, auth);
        return WebResponse.<EmployeeResponse>builder().data(employeeResponse).build();
    }
    @PreAuthorize("hasAuthority('user')")
    @PatchMapping(path = "/current", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<EmployeeResponse> update(@RequestBody EmployeeRequest request, @AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal, Authentication auth){
        validateAuthentication(auth);
        EmployeeResponse employeeResponse = employeeService.update(request, principal);
        return WebResponse.<EmployeeResponse>builder().data(employeeResponse).build();
    }
    @PreAuthorize("hasAuthority('admin') or hasAuthority('manager')")
    @GetMapping(path = "/{clientId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<EmployeeResponse> findByClientId(@PathVariable("clientId") String clientId) {
        EmployeeResponse employeeResponse = employeeService.getByClientId(clientId);
        return WebResponse.<EmployeeResponse>builder().data(employeeResponse).build();
    }
    @PreAuthorize("hasAuthority('admin') or hasAuthority('manager')")
    @GetMapping(path = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<List<EmployeeResponse>> getAll(@AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal, Authentication auth) {
       validateAuthentication(auth);
        List<EmployeeResponse> employeeResponseAll = employeeService.findAllEmployee(principal, auth);
        return WebResponse.<List<EmployeeResponse>>builder().data(employeeResponseAll).build();
    }
    private void validateAuthentication(Authentication auth) {
        if (!auth.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not authenticated");
        }
    }
}
