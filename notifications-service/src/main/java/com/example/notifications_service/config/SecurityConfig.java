package com.example.notifications_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration for Notification Service
 * - REST API: CORS handled by Gateway (requests come through Gateway)
 * - WebSocket: CORS handled by WebSocketConfig (direct connections from frontend)
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

        @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            // Disable CORS for REST APIs (handled by Gateway)
            // CORS for WebSocket is handled in WebSocketConfig
            .cors(cors -> cors.disable())
            .authorizeHttpRequests(auth -> auth
                // Public endpoints
                .requestMatchers("/actuator/**").permitAll()
                .requestMatchers("/h2-console/**").permitAll()
                
                // WebSocket endpoints - allow for SockJS handshake and direct connections
                .requestMatchers("/ws/**").permitAll()
                
                // All notification API endpoints require authentication
                .requestMatchers("/api/notifications/**").authenticated()
                
                // All other requests require authentication
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> {})
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            );
        
        return http.build();
    }
}

