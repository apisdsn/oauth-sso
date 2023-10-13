package demo.app.utils;

import demo.app.utils.AuthoritiesManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.parameters.P;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2TokenIntrospectionClaimNames;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Slf4j
public class AuthoritiesExtractor {


    private final AuthoritiesManager authoritiesManager;

    @Value("${zitadel.iam.org.project.roles-attribute}")
    private String ROLES_ATTRIBUTE;

    @Autowired
    public AuthoritiesExtractor(AuthoritiesManager authoritiesManager) {
        this.authoritiesManager = authoritiesManager;
    }

    public Collection<GrantedAuthority> extractAuthorities(OAuth2AuthenticatedPrincipal principal) {
        List<String> scopes = principal.getAttribute(OAuth2TokenIntrospectionClaimNames.SCOPE);
        List<String> userAuthorities = getUserAuthorities(principal);

        log.debug("Scopes: {}", scopes);
        log.debug("User Authorities: {}", userAuthorities);


        assert scopes != null;

        List<String> allAuthorities = Stream.concat(scopes.stream(), userAuthorities.stream())
                .collect(Collectors.toList());
        log.debug("All Authorities: {}", allAuthorities );

        authoritiesManager.setAllAuthorities(allAuthorities);

        return allAuthorities.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    private List<String> getUserAuthorities(OAuth2AuthenticatedPrincipal principal) {
        Map<String, Map<String, String>> projectRoles = principal.getAttribute(ROLES_ATTRIBUTE);
        if (projectRoles == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Project Roles Attribute is null");
        }
        return projectRoles.keySet().stream().toList();
    }
}
