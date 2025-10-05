package com.gateway.filter;

import org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

import java.util.stream.Collectors;

import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.addRequestHeader;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.cloud.gateway.server.mvc.predicate.GatewayRequestPredicates.path;

/**
 * Gateway Filter Configuration
 * Adds custom headers to requests before forwarding to backend services:
 * - X-User-Id: Keycloak user ID (sub claim)
 * - X-User-Email: User email
 * - X-User-Roles: Comma-separated list of roles
 * - X-User-Preferred-Username: Username
 */
@Configuration
public class GatewayFilterConfig {

    /**
     * Add user information headers to all requests
     * This allows backend services to identify users without parsing JWT again
     */
    private ServerRequest addUserInfoHeaders(ServerRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            Jwt jwt = jwtAuth.getToken();
            
            // Extract user information from JWT
            String userId = jwt.getSubject(); // Keycloak user ID
            String email = jwt.getClaimAsString("email");
            String preferredUsername = jwt.getClaimAsString("preferred_username");
            
            // Extract roles
            String roles = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.joining(","));
            
            // Add headers to request
            ServerRequest.Builder builder = ServerRequest.from(request);
            
            if (userId != null) {
                builder.header("X-User-Id", userId);
            }
            if (email != null) {
                builder.header("X-User-Email", email);
            }
            if (preferredUsername != null) {
                builder.header("X-User-Preferred-Username", preferredUsername);
            }
            if (roles != null && !roles.isEmpty()) {
                builder.header("X-User-Roles", roles);
            }
            
            return builder.build();
        }
        
        return request;
    }

    /**
     * Configure routes with custom filters
     * All routes will have user information headers added
     */
    @Bean
    public RouterFunction<ServerResponse> gatewayRoutes() {
        return route("policy-service")
                .route(path("/api/policies/**"), http("http://localhost:8081"))
                .before(this::addUserInfoHeaders)
                .build()
            .and(route("claims-service-user")
                .route(path("/api/claims/**"), http("http://localhost:8082"))
                .before(this::addUserInfoHeaders)
                .build())
            .and(route("claims-service-admin")
                .route(path("/api/admin/claims/**"), http("http://localhost:8082"))
                .before(this::addUserInfoHeaders)
                .build());
    }
}
