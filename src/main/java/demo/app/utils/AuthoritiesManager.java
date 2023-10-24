//package demo.app.utils;
//
//import lombok.Getter;
//import lombok.Setter;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.HttpStatus;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
//import org.springframework.stereotype.Component;
//import org.springframework.web.server.ResponseStatusException;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Getter
//@Setter
//@Component
//@Slf4j
//public class AuthoritiesManager {
//
//    private List<String> allAuthorities;
//
//    public boolean hasAuthority(OAuth2AuthenticatedPrincipal principal, Authentication auth) {
//        List<? extends GrantedAuthority> userAuthorities = principal.getAuthorities().stream().toList();
//
//        List<String> authAuthorities = auth.getAuthorities().stream()
//                .map(GrantedAuthority::getAuthority)
//                .toList();
//
//        log.debug("userAuthorities: {}", userAuthorities);
//        log.debug("authAuthorities: {}", authAuthorities);
//
//        return !userAuthorities.stream()
//                .map(GrantedAuthority::getAuthority)
//                .allMatch(authAuthorities::contains);
//    }
//
//    public boolean checkIfUserIsAdminOrManager(OAuth2AuthenticatedPrincipal principal) {
//        List<GrantedAuthority> authorities = new ArrayList<>(principal.getAuthorities());
//
//        List<String> allowedAuthorities = List.of("admin", "manager");
//
//        return authorities.stream()
//                .map(GrantedAuthority::getAuthority)
//                .noneMatch(allowedAuthorities::contains);
//    }
//
//    public void validateAuthentication(Authentication auth) {
//        if (auth == null || !auth.isAuthenticated()) {
//            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not authenticated");
//
//        }
//    }
//}
