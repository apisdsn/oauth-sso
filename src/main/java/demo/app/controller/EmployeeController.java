package demo.app.controller;

import demo.app.model.EmployeeRequest;
import demo.app.model.EmployeeResponse;
import demo.app.model.WebResponse;
import demo.app.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(path = "/api/employee/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<String> register(@RequestBody EmployeeRequest request, @AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal, Authentication auth) {
        employeeService.register(request, principal, auth);
        return WebResponse.<String>builder().data("Data has store in database").build();
    }

    @GetMapping(path = "/api/employee/current", produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<EmployeeResponse> get(@AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal) {
        EmployeeResponse employeeResponse = employeeService.getCurrent(principal);
        return WebResponse.<EmployeeResponse>builder().data(employeeResponse).build();
    }

    @PutMapping(path = "/api/employee/current", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<EmployeeResponse> update(@RequestBody EmployeeRequest request, @AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal) {
        EmployeeResponse employeeResponse = employeeService.update(request, principal);
        return WebResponse.<EmployeeResponse>builder().data(employeeResponse).build();
    }

    @DeleteMapping(path = "/api/employee/current", produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<String> remove(@AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal) {
        employeeService.removeCurrent(principal);
        return WebResponse.<String>builder().data("Data has removed in database").build();
    }

    // admin or manager controller
    @GetMapping(path = "/api/admin/employee/{clientId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<EmployeeResponse> findByClientId(@PathVariable("clientId") String clientId, @AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal) {
        EmployeeResponse employeeResponse = employeeService.getByClientId(clientId, principal);
        log.debug("employeeResponse: {}", employeeResponse);
        return WebResponse.<EmployeeResponse>builder().data(employeeResponse).build();
    }

    // admin or manager controller
    @GetMapping(path = "/api/admin/employee/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<List<EmployeeResponse>> getAll(@AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal) {
        List<EmployeeResponse> employeeResponseAll = employeeService.findAllEmployee(principal);
        log.debug("employeeResponseAll: {}", employeeResponseAll);
        return WebResponse.<List<EmployeeResponse>>builder().data(employeeResponseAll).build();
    }

    // admin or manager controller
    @DeleteMapping(path = "/api/admin/employee/{clientId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<String> remove(@PathVariable("clientId") String clientId, @AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal) {
        employeeService.removeByClientId(clientId, principal);
        return WebResponse.<String>builder().data("Data with clientId has removed").build();
    }
}
