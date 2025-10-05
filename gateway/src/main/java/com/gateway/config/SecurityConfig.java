package com.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Security configuration for API Gateway
 * - First line of defense for all requests
 * - Extracts roles from Keycloak JWT token
 * - Routes to appropriate microservices
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    /**
     * Configure security filter chain for the gateway
     * This is the first line of defense - validates JWT and extracts roles
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeHttpRequests(auth -> auth
                // Public endpoints
                .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                .requestMatchers("/actuator/gateway/**", "/actuator/routes").permitAll()
                
                // Admin-only endpoints - enforce ADMIN role at gateway level
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                
                // User endpoints - require authentication
                .requestMatchers("/api/policies/**").authenticated()
                .requestMatchers("/api/claims/**").authenticated()
                
                // All other requests require authentication
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            );
        
        return http.build();
    }
    
    /**
     * Configure JWT authentication converter to extract roles from Keycloak token
     * Roles are extracted from realm_access.roles claim
     */
    @Bean
    public Converter<Jwt, AbstractAuthenticationToken> jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(new KeycloakRealmRoleConverter());
        return converter;
    }
    
    /**
     * Converter to extract Keycloak realm roles from JWT token
     * Converts roles to Spring Security GrantedAuthority with ROLE_ prefix
     * Supports both 'user' and 'admin' roles, converting to uppercase
     */
    static class KeycloakRealmRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
        @Override
        public Collection<GrantedAuthority> convert(Jwt jwt) {
            if (jwt.getClaims() == null) {
                return Collections.emptyList();
            }
            
            // Extract realm_access claim from JWT
            final Map<String, Object> realmAccess = (Map<String, Object>) jwt.getClaim("realm_access");
            if (realmAccess == null || realmAccess.isEmpty()) {
                return Collections.emptyList();
            }
            
            // Extract roles array
            Collection<String> roles = (Collection<String>) realmAccess.get("roles");
            if (roles == null || roles.isEmpty()) {
                return Collections.emptyList();
            }
            
            // Convert roles to GrantedAuthority with ROLE_ prefix and uppercase
            // Examples: "user" -> "ROLE_USER", "admin" -> "ROLE_ADMIN"
            return roles.stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                    .collect(Collectors.toList());
        }
    }
    
    /**
     * Configure CORS for the gateway
     * Allows requests from Angular frontend on localhost:4200
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
