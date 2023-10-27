package demo.app.utils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

@Slf4j
public class CustomAuthoritiesFilter extends GenericFilterBean {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String requestedPath = request.getRequestURI();
        Map<String, List<String>> rolePathsMap = new HashMap<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof OAuth2AuthenticatedPrincipal principal) {
            Collection<? extends GrantedAuthority> authorities = principal.getAuthorities();
            List<String> roles = authorities.stream().map(GrantedAuthority::getAuthority).toList();
            log.info("Roles: {}", roles);

            if (requestedPath.startsWith("/api/admin") || requestedPath.startsWith("/**")) {
                rolePathsMap.put("ROLE_ADMIN", List.of(requestedPath));
                rolePathsMap.put("ROLE_MANAGER", List.of(requestedPath));
            } else if (requestedPath.startsWith("/api/employee") || requestedPath.startsWith("/api/address") || requestedPath.startsWith("/api/reimbursement")) {
                rolePathsMap.put("ROLE_USER", List.of(requestedPath));
            } else {
                throw new ServletException("Invalid path");
            }

            boolean isPathAllowed = roles.stream()
                    .anyMatch(role -> rolePathsMap.getOrDefault(role, Collections.emptyList()).stream()
                            .anyMatch(requestedPath::startsWith));

            log.info("isPathAllowed : {} ", isPathAllowed);

            if (isPathAllowed) {
                filterChain.doFilter(request, response);
            } else {
                setForbidden(response);
            }
        } else {
            setUnauthorized(response);
        }
    }

    private void setUnauthorized(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        PrintWriter writer = response.getWriter();
        writer.println("{ \"error\": \"You are not authenticated to perform this operation\" }");
        writer.flush();
    }

    private void setForbidden(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        PrintWriter writer = response.getWriter();
        writer.println("{ \"error\": \"You are not have Access to resource. Access denied\" }");
        writer.flush();
    }
}