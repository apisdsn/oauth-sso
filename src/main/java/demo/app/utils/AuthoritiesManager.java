package demo.app.utils;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Component
public class AuthoritiesManager {
    private List<String> allAuthorities;

    public boolean hasAuthority(OAuth2AuthenticatedPrincipal principal, Authentication auth) {
        List<? extends GrantedAuthority> userAuthorities = principal.getAuthorities().stream().toList();
        return userAuthorities.stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.equals(auth.getAuthorities().toString()));
    }

}
