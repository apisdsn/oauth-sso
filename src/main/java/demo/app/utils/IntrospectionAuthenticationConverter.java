//package demo.app.config;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.oauth2.core.OAuth2AccessToken;
//import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
//import org.springframework.security.oauth2.core.OAuth2TokenIntrospectionClaimNames;
//import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;
//import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;
//import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenAuthenticationConverter;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//import java.util.Map;
//import java.util.Optional;
//import java.util.stream.Collectors;
//
//@Slf4j
//@Configuration
//@Component
//public class IntrospectionAuthenticationConverter implements OpaqueTokenAuthenticationConverter {
//
//     final String ROLES_ATTRIBUTE = "urn:zitadel:iam:org:project:233630462214406147:roles";
//
//    @Override
//    public Authentication convert(String introspectedToken, OAuth2AuthenticatedPrincipal authenticatedPrincipal) {
//        // Mendapatkan roles dari atribut token
//        Map<String, Map<String, String>> rolesClaim = authenticatedPrincipal.getAttribute(ROLES_ATTRIBUTE);
//
//        // Membuat authorities dari roles yang ditemukan
//        List<SimpleGrantedAuthority> authorities = Optional.ofNullable(rolesClaim)
//                .map(Map::keySet)
//                .map(keys -> keys.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()))
//                .orElse(List.of());
//
//        // Debugging: Mencetak authorities dan informasi lainnya
//        log.debug("Authorities: {}", authorities);
//        log.debug("IdTokenClaim CLIENT_ID: {}", Optional.ofNullable(authenticatedPrincipal.getAttribute(OAuth2TokenIntrospectionClaimNames.CLIENT_ID)));
//        log.debug("IdTokenClaim IAT: {}", Optional.ofNullable(authenticatedPrincipal.getAttribute(IdTokenClaimNames.IAT)));
//        log.debug("IdTokenClaim EXP: {}", Optional.ofNullable(authenticatedPrincipal.getAttribute(IdTokenClaimNames.EXP)));
//
//        // Membuat objek BearerTokenAuthentication dengan authorities yang sudah diperoleh
//        return new BearerTokenAuthentication(
//                authenticatedPrincipal,
//                new OAuth2AccessToken(
//                        OAuth2AccessToken.TokenType.BEARER,
//                        introspectedToken,
//                        authenticatedPrincipal.getAttribute(IdTokenClaimNames.IAT),
//                        authenticatedPrincipal.getAttribute(IdTokenClaimNames.EXP)
//                ),
//                authorities
//        );
//    }
//}
