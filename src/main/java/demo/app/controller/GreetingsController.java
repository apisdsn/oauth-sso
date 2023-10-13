package demo.app.controller;

import demo.app.utils.AuthoritiesManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
class GreetingsController {

    @GetMapping("/api/greet/admin")
    @PreAuthorize("hasAuthority('admin')")
    Object greetmeadmin(Authentication auth) {

        var getAuthor = auth.getAuthorities();
        var tokenAttributes = ((BearerTokenAuthentication) auth).getTokenAttributes();
        var message = "Endpoint Access for Admin Greetings my friend " + tokenAttributes.get(StandardClaimNames.PREFERRED_USERNAME) + " " + Instant.now() + " " + getAuthor;
        return Map.of("message", message);
    }
    @GetMapping("/api/greet/client")
    @PreAuthorize("hasAuthority('user')")
    Object greetmeclient(Authentication auth) {
        var getAuthor = auth.getAuthorities();
        var tokenAttributes = ((BearerTokenAuthentication) auth).getTokenAttributes();
        var message = "Endpoint Access for User Greetings my friend " + tokenAttributes.get(StandardClaimNames.PREFERRED_USERNAME) + " " + Instant.now() + " " + getAuthor ;
        return Map.of("message", message);
    }
}
