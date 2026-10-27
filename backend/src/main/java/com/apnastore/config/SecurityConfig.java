package com.apnastore.config;

import com.apnastore.dto.ErrorResponse;
import com.apnastore.security.JwtAuthFilter;
import com.apnastore.security.UserDetailsServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security configuration.
 *
 * Access rules (requirement #5):
 *   PUBLIC   : GET /api/products/**, /api/auth/**, /h2-console/**, frontend static files
 *   AUTH     : POST /api/orders, GET /api/orders/my
 *   ADMIN    : POST/PUT/DELETE /api/products/**
 *
 * 401 / 403 responses are returned as JSON using the same ErrorResponse format
 * as the global exception handler (requirement #7).
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF — we use stateless JWT, not session cookies
            .csrf(AbstractHttpConfigurer::disable)

            // Allow H2 console to load inside iframes (dev only)
            .headers(headers -> headers
                .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
            )

            // Stateless — no HTTP sessions
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // Authorization rules
            .authorizeHttpRequests(auth -> auth
                // Auth endpoints — always public
                .requestMatchers("/api/auth/**").permitAll()

                // Product reads — public; writes — ADMIN only
                .requestMatchers(HttpMethod.GET,    "/api/products/**").permitAll()
                .requestMatchers(HttpMethod.POST,   "/api/products/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT,    "/api/products/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/products/**").hasRole("ADMIN")

                // Order — authenticated users
                .requestMatchers(HttpMethod.POST, "/api/orders").authenticated()
                .requestMatchers(HttpMethod.GET,  "/api/orders/my").authenticated()

                // H2 web console (dev only)
                .requestMatchers("/h2-console/**").permitAll()

                // Frontend static files (index.html, app.js, styles.css, /assets/*)
                .requestMatchers(
                    "/", "/index.html", "/app.js", "/data.js",
                    "/styles.css", "/assets/**", "/*.png", "/*.ico"
                ).permitAll()

                // Any other request requires authentication
                .anyRequest().authenticated()
            )

            // Clean JSON 401 — not logged in
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");
                    ErrorResponse body = new ErrorResponse(401, "Authentication required: " + authException.getMessage());
                    response.getWriter().write(objectMapper.writeValueAsString(body));
                })
                // Clean JSON 403 — logged in but not allowed
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.setContentType("application/json");
                    ErrorResponse body = new ErrorResponse(403, "Access denied: you do not have permission for this action");
                    response.getWriter().write(objectMapper.writeValueAsString(body));
                })
            )

            // Wire the JWT filter before the standard auth filter
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)

            .authenticationProvider(authenticationProvider());

        return http.build();
    }
}
