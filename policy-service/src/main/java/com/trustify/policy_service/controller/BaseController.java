package com.trustify.policy_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.HashMap;
import java.util.Map;

/**
 * Base controller with common endpoints
 */
@RestController
public class BaseController {

    /**
     * Health check endpoint
     *
     * @return health status
     */
    @GetMapping("/api/public/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Policy Service");
        return ResponseEntity.ok(response);
    }
    
    /**
     * Authentication test endpoint
     *
     * @return authentication information
     */
    @GetMapping("/api/auth-info")
    public ResponseEntity<Map<String, Object>> getAuthInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> response = new HashMap<>();
        
        if (authentication != null && authentication.isAuthenticated()) {
            response.put("authenticated", true);
            response.put("principal", authentication.getPrincipal());
            response.put("name", authentication.getName());
            response.put("authorities", authentication.getAuthorities());
            
            if (authentication.getPrincipal() instanceof Jwt) {
                Jwt jwt = (Jwt) authentication.getPrincipal();
                response.put("sub", jwt.getSubject());
                response.put("tokenAttributes", jwt.getClaims());
            }
        } else {
            response.put("authenticated", false);
        }
        
        return ResponseEntity.ok(response);
    }
}