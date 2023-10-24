package demo.app.security;

import demo.app.utils.CustomAuthoritiesFilter;
import demo.app.utils.CustomAuthoritiesOpaqueTokenIntrospector;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Slf4j
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
    private final String[] AUTH_WHITELIST = {
            "/v3/api-docs/**",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/websocket/**",
            "/webjars/**",
            "/stomp/**",
            "/app.js",
            "/main.css",
    };

    @Bean
    public OpaqueTokenIntrospector customAuthoritiesOpaqueTokenIntrospector() {
        return new CustomAuthoritiesOpaqueTokenIntrospector();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .addFilterAfter(new CustomAuthoritiesFilter(), BasicAuthenticationFilter.class)
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers(AUTH_WHITELIST).permitAll()
                                .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2ResourceServer ->
                        oauth2ResourceServer
                                .opaqueToken(opaqueTokenConfigurer ->
                                        opaqueTokenConfigurer
                                                .introspector(customAuthoritiesOpaqueTokenIntrospector())
                                )
                )
                .sessionManagement(smc ->
                        smc
                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );
        return http.build();
    }
}
