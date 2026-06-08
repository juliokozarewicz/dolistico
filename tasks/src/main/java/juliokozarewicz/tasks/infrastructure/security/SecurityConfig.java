package juliokozarewicz.tasks.infrastructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    // ======================================================= (Beans help init)
    @Bean
    public AuthenticationFilter authenticationFilter() {
        return new AuthenticationFilter();
    }

    @Bean
    public BearerTokenResolver bearerTokenResolver(AuthenticationFilter filter) {
        return request -> {
            Object token = request.getAttribute("DECRYPTED_JWT");
            return token != null ? token.toString() : null;
        };
    }
    // ======================================================= (Beans help init)

    // ========================================================== (Methods init)
    @Bean
    public SecurityFilterChain filterChain(

        HttpSecurity http,
        BearerTokenResolver bearerTokenResolver,
        AuthenticationFilter authenticationFilter

    ) throws Exception {

        List<String> publicPaths = authenticationFilter.getPublicPaths();

        return http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .authorizeHttpRequests(authz -> authz
                    .requestMatchers(publicPaths.toArray(new String[0])).permitAll()
                    .anyRequest().authenticated()
                )
            .oauth2ResourceServer(
    oauth -> oauth
                .bearerTokenResolver(bearerTokenResolver)
                .jwt(jwt -> {})
                .authenticationEntryPoint((request, response, ex) -> {
                    response.setStatus(401);
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write(
                        "{\"timestamp\":\"" + java.time.Instant.now()
                            .truncatedTo(java.time.temporal.ChronoUnit.SECONDS) + "\"" +
                        ",\"statusCode\":401" +
                        ",\"messageCode\":\"INVALID_CREDENTIALS\"}"
                    );
                })
        )
            .build();
    }
    // =========================================================== (Methods end)

}