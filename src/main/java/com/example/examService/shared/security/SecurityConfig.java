package com.example.examService.shared.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * SecurityConfig - Spring Security configuration for ExamService
 * 
 * Architecture:
 * - Gateway validates JWT and sets headers (X-User-Id, X-User-Email,
 * X-User-Role)
 * - ExamService reads headers via GatewayHeaderAuthenticationFilter
 * - Stateless authentication (no server sessions)
 * 
 * Endpoints:
 * - /api/exams/** → USER role (exam operations)
 * - /api/admin/exams/** → ADMIN role (system statistics)
 * 
 * Security Model:
 * - JWT validation happens at Gateway (not here)
 * - We trust headers from Gateway
 * - No CSRF needed (stateless JWT)
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private GatewayHeaderAuthenticationFilter gatewayHeaderAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF (stateless JWT authentication)
                .csrf(csrf -> csrf.disable())

                // Configure endpoint authorization
                .authorizeHttpRequests(authz -> authz
                        // ADMIN ENDPOINTS - Must come FIRST (most specific)
                        .requestMatchers("/api/admin/exams/**").hasRole("ADMIN")

                        // USER ENDPOINTS - Standard exam operations
                        .requestMatchers("/api/exams/**").hasRole("USER")

                        // Deny everything else
                        .anyRequest().denyAll())

                // Stateless sessions - using JWT instead of server sessions
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Add Gateway header filter to read X-User-* headers
                .addFilterBefore(
                        gatewayHeaderAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class)

                // Exception handling
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(401);
                            response.setContentType("application/json");
                            response.getWriter().write(
                                    "{\"error\": \"Unauthorized\", \"message\": \"JWT token required\"}");
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(403);
                            response.setContentType("application/json");
                            response.getWriter().write(
                                    "{\"error\": \"Forbidden\", \"message\": \"Insufficient permissions\"}");
                        }));

        return http.build();
    }
}
