package com.example.examService.Security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

        /**
         * =========================================================================
         * REFACTORED SECURITY ARCHITECTURE - RESTful Endpoints
         * =========================================================================
         * 
         * Gateway validates JWT and sets headers: X-User-Id, X-User-Email, X-User-Role
         * ExamService reads headers via GatewayHeaderAuthenticationFilter
         * 
         * ENDPOINT STRUCTURE:
         * - /api/exams/** → USER endpoints (exam management)
         * - /api/admin/exams/** → ADMIN endpoints (system statistics, user monitoring)
         * 
         * Clear separation between user operations and administrative operations.
         */
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
                                                // System-wide exam statistics and user monitoring
                                                .requestMatchers("/api/admin/exams/**")
                                                .hasRole("ADMIN")

                                                // USER ENDPOINTS - Standard exam operations
                                                // All exam CRUD operations for authenticated users
                                                .requestMatchers("/api/exams/**")
                                                .hasRole("USER")

                                                // Deny everything else
                                                .anyRequest().denyAll())

                                // FÖRKLARING: Stateless sessions - vi använder JWT istället för server sessions
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                                // FÖRKLARING: Lägg till Gateway header filter som läser X-User-* headers
                                .addFilterBefore(gatewayHeaderAuthenticationFilter,
                                                UsernamePasswordAuthenticationFilter.class)

                                // FÖRKLARING: Hantera unauthorized requests
                                .exceptionHandling(exceptions -> exceptions
                                                .authenticationEntryPoint((request, response, authException) -> {

                                                        // Returnera 401 Unauthorized med custom meddelande
                                                        response.setStatus(401);
                                                        response.setContentType("application/json");
                                                        response.getWriter().write(
                                                                        "{\"error\": \"Unauthorized\", \"message\": \"JWT token krävs för denna endpoint\"}");
                                                })
                                                .accessDeniedHandler((request, response, accessDeniedException) -> {

                                                        // Returnera 403 Forbidden när användaren saknar rätt roll
                                                        response.setStatus(403);
                                                        response.setContentType("application/json");
                                                        response.getWriter().write(
                                                                        "{\"error\": \"Forbidden\", \"message\": \"Du har inte behörighet att komma åt denna resurs\"}");
                                                }));

                return http.build();
        }
}
