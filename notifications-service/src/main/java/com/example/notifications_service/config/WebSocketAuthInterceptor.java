package com.example.notifications_service.config;

import java.util.Collections;
import java.util.List;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Interceptor to authenticate WebSocket connections using JWT token
 */
@Component
@Slf4j
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    private final JwtDecoder jwtDecoder;

    public WebSocketAuthInterceptor(JwtDecoder jwtDecoder) {
        this.jwtDecoder = jwtDecoder;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            // Extract JWT token from Authorization header
            List<String> authorization = accessor.getNativeHeader("Authorization");
            
            if (authorization != null && !authorization.isEmpty()) {
                String token = authorization.get(0);
                
                // Remove "Bearer " prefix if present
                if (token.startsWith("Bearer ")) {
                    token = token.substring(7);
                }
                
                try {
                    // Decode and validate JWT token
                    Jwt jwt = jwtDecoder.decode(token);
                    
                    // Extract user ID from JWT 'sub' claim (Keycloak user ID)
                    String userId = jwt.getSubject();
                    
                    log.info("WebSocket authentication successful for user: {}", userId);
                    
                    // Create authentication object with user ID as principal
                    Authentication authentication = new UsernamePasswordAuthenticationToken(
                            userId,  // Principal is the Keycloak user ID
                            null,
                            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
                    );
                    
                    // Set the user for this WebSocket session
                    accessor.setUser(authentication);
                    
                } catch (JwtException e) {
                    log.error("Invalid JWT token for WebSocket connection: {}", e.getMessage());
                    return null; // Reject the connection
                }
            } else {
                log.warn("No Authorization header found in WebSocket CONNECT frame");
                return null; // Reject the connection
            }
        }
        
        return message;
    }
}
