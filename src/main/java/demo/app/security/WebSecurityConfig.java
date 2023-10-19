package demo.app.security;

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

@Slf4j
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    public OpaqueTokenIntrospector customAuthoritiesOpaqueTokenIntrospector() {
        return new CustomAuthoritiesOpaqueTokenIntrospector();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/api/greet/admin").hasAuthority("admin")
                                .requestMatchers("/api/greet/client").hasAuthority("user")
                                .requestMatchers("/api/employees/**").permitAll()
                                .requestMatchers("/api/address/**").permitAll()
                                .requestMatchers("/api/reimbursement/**").permitAll()
                                .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2ResourceServer ->
                        oauth2ResourceServer
                                .opaqueToken(opaqueTokenConfigurer ->
                                        opaqueTokenConfigurer
                                                .introspector(customAuthoritiesOpaqueTokenIntrospector())
                                )
                )
                .exceptionHandling(Customizer.withDefaults())
                .sessionManagement(smc ->
                        smc
                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );
        return http.build();
    }
}
