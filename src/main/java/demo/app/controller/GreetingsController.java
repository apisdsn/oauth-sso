package demo.app.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

@RestController
@Slf4j
class GreetingsController {

    @GetMapping("/api/greet/admin")
    Object greetmeadmin(Authentication auth) {
        var getAuthor = auth.getAuthorities();
        var tokenAttributes = ((BearerTokenAuthentication) auth).getTokenAttributes();
        var message = "Endpoint Access for Admin Greetings my friend " + tokenAttributes.get(StandardClaimNames.PREFERRED_USERNAME) + " " + Instant.now() + " " + getAuthor;
        return Map.of("message", message);
    }

    @GetMapping("/api/greet/client")
    Object greetmeclient(Authentication auth) {
        var getAuthor = auth.getAuthorities();
        var tokenAttributes = ((BearerTokenAuthentication) auth).getTokenAttributes();
        var message = "Endpoint Access for User Greetings my friend " + tokenAttributes.get(StandardClaimNames.PREFERRED_USERNAME) + " " + Instant.now() + " " + getAuthor;
        return Map.of("message", message);
    }
}
