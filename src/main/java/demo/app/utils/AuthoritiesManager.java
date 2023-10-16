package demo.app.utils;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Component
public class AuthoritiesManager {

    private List<String> allAuthorities;

    public boolean hasAuthority(OAuth2AuthenticatedPrincipal principal, Authentication auth) {
        List<? extends GrantedAuthority> userAuthorities = principal.getAuthorities().stream().toList();
        return userAuthorities.stream()
                .map(GrantedAuthority::getAuthority)
                .allMatch(authority -> authority.equals(auth.getAuthorities().toString()));
    }

    public boolean checkIfUserIsAdminOrManager(OAuth2AuthenticatedPrincipal principal) {
        List<GrantedAuthority> authorities = new ArrayList<>(principal.getAuthorities());

        List<String> allowedAuthorities = List.of("admin", "manager");

        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .noneMatch(allowedAuthorities::contains);
    }
}



