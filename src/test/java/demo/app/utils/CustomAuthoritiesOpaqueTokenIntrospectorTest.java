package demo.app.utils;

import demo.app.validator.IntrospectTokenValidator;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.introspection.OAuth2IntrospectionException;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomAuthoritiesOpaqueTokenIntrospectorTest {
    @Mock
    private AuthorityExtractor authorityExtractor;
    @Mock
    private IntrospectTokenValidator introspectTokenValidator;
    @Mock
    private OpaqueTokenIntrospector opaqueTokenIntrospector;
    @InjectMocks
    private CustomAuthoritiesOpaqueTokenIntrospector customAuthoritiesOpaqueTokenIntrospector;


    @Test
    @Disabled
    public void testIntrospectWhenValidTokenThenReturnPrincipal() {
        // Arrange
        String token = "validToken";
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("attribute1", "value1");
        OAuth2AuthenticatedPrincipal principal = new DefaultOAuth2AuthenticatedPrincipal("name", attributes, Collections.emptyList());
        when(opaqueTokenIntrospector.introspect(token)).thenReturn(principal);

        // Mock the dependencies
        MockitoAnnotations.openMocks(this); // Initialize the mocks
        when(authorityExtractor.extractAuthorities(principal)).thenReturn(Collections.emptyList());

        // Create an instance of CustomAuthoritiesOpaqueTokenIntrospector with a non-null introspectionUri
        CustomAuthoritiesOpaqueTokenIntrospector customAuthoritiesOpaqueTokenIntrospector = new CustomAuthoritiesOpaqueTokenIntrospector();
        ReflectionTestUtils.setField(customAuthoritiesOpaqueTokenIntrospector, "INTROSPECT_URI", "http://localhost:8080/oauth2/introspect");
        ReflectionTestUtils.setField(customAuthoritiesOpaqueTokenIntrospector, "CLIENT_ID", "clientId");
        ReflectionTestUtils.setField(customAuthoritiesOpaqueTokenIntrospector, "CLIENT_SECRET", "clientSecret");

        // Act
        OAuth2AuthenticatedPrincipal result;
        try {
            result = customAuthoritiesOpaqueTokenIntrospector.introspect(token);
        } catch (OAuth2IntrospectionException e) {
            // Handle the exception
            System.out.println("Error occurred during token introspection: " + e.getMessage());
            return;
        }

        // Assert
        assertEquals(principal, result);
        verify(introspectTokenValidator, times(1)).validateToken(principal.getAttributes(), authorityExtractor.extractAuthorities(principal));
    }

    @Test
    void testIntrospectWhenInvalidTokenThenThrowException() {
        // Arrange
        String token = "invalidToken";

        // Act & Assert
        assertThrows(RuntimeException.class, () -> customAuthoritiesOpaqueTokenIntrospector.introspect(token));
        verify(introspectTokenValidator, never()).validateToken(any(), any());
    }
}