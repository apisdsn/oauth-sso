package demo.app.validator;

import demo.app.exception.ValidatorErrorHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Collection;
import java.util.Map;

@Slf4j
@Component
public class IntrospectTokenValidator {

    @Value("${zitadel.iam.org.project.roles-attribute}")
    private String ROLES_ATTRIBUTE;
    public void validateToken(Map<String, Object> token, Collection<GrantedAuthority> scopes) {
        log.debug("Token: {}", token);

        Instant expirationTime = getExpirationTime(token);
        if (isTokenInvalidOrExpired(token, expirationTime) || !matchTokenScopes(token, scopes)) {
            throw new ValidatorErrorHandler(HttpStatus.UNAUTHORIZED, "invalid_token_revoked", "Token is invalid or expired.");
        }
    }
    private Instant getExpirationTime(Map<String, Object> token) {
        Object expValue = token.get("exp");
        log.debug("Exp Value {}", expValue);

        if (expValue instanceof Number) {
            return Instant.ofEpochSecond(((Number) expValue).longValue());
        } else if (expValue instanceof Instant) {
            return (Instant) expValue;
        } else {
            throw new IllegalArgumentException("Invalid 'exp' value in token.");
        }
    }
    private boolean isTokenInvalidOrExpired(Map<String, Object> token, Instant expirationTime) {
        return expirationTime.isBefore(Instant.now()) || !(boolean) token.getOrDefault("active", false);
    }
    @SuppressWarnings("unchecked")
    private boolean matchTokenScopes(Map<String, Object> token, Collection<GrantedAuthority> orScopes) {
        log.debug("Match Token Scopes: {}", orScopes);
        if (orScopes == null) {
            return true;
        }

        Object rolesObject = token.get(ROLES_ATTRIBUTE);
        log.debug("Roles Object: {}", rolesObject);
        if (!(rolesObject instanceof Map)) {
            return false;
        }

        Map<String, Map<String, String>> projectRoles = (Map<String, Map<String, String>>) rolesObject;
        return orScopes.stream().anyMatch(scope -> projectRoles.containsKey(scope.getAuthority()));
    }
}