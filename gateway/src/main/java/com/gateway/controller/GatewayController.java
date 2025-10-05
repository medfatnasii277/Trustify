package com.gateway.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Gateway Controller
 * Provides information about the gateway and current user
 */
@RestController
@RequestMapping("/gateway")
public class GatewayController {

    /**
     * Get gateway information
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("service", "API Gateway");
        info.put("version", "1.0.0");
        info.put("description", "Trustify API Gateway - Routes requests to policy-service and claims-service");
        info.put("timestamp", Instant.now());
        
        Map<String, String> routes = new HashMap<>();
        routes.put("policies", "http://localhost:8081/api/policies");
        routes.put("claims", "http://localhost:8082/api/claims");
        routes.put("admin-claims", "http://localhost:8082/api/admin/claims");
        info.put("routes", routes);
        
        return ResponseEntity.ok(info);
    }

    /**
     * Get current user information from JWT token
     * Useful for debugging authentication
     */
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            Jwt jwt = jwtAuth.getToken();
            
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("userId", jwt.getSubject());
            userInfo.put("email", jwt.getClaimAsString("email"));
            userInfo.put("username", jwt.getClaimAsString("preferred_username"));
            userInfo.put("name", jwt.getClaimAsString("name"));
            userInfo.put("roles", authentication.getAuthorities().stream()
                    .map(a -> a.getAuthority())
                    .collect(Collectors.toList()));
            userInfo.put("tokenIssuedAt", jwt.getIssuedAt());
            userInfo.put("tokenExpiresAt", jwt.getExpiresAt());
            
            return ResponseEntity.ok(userInfo);
        }
        
        return ResponseEntity.status(401).body(Map.of("error", "No authentication found"));
    }
}
