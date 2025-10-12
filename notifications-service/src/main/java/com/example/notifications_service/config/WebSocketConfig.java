package com.example.notifications_service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import lombok.RequiredArgsConstructor;

/**
 * WebSocket configuration for real-time notifications
 * Enables STOMP protocol over WebSocket
 */
@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final WebSocketAuthInterceptor webSocketAuthInterceptor;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Enable a simple memory-based message broker to carry messages back to clients
        // on destinations prefixed with "/topic" and "/user"
        config.enableSimpleBroker("/topic", "/user");
        
        // Set application destination prefix for methods annotated with @MessageMapping
        config.setApplicationDestinationPrefixes("/app");
        
        // Set prefix for user-specific destinations
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Register the /ws endpoint for WebSocket connections
        // Allow all origins since requests come through API Gateway which handles CORS
        // Enable SockJS fallback options for browsers that don't support WebSocket
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
        
        // Also register without SockJS for native WebSocket support
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        // Add authentication interceptor to validate JWT tokens
        registration.interceptors(webSocketAuthInterceptor);
    }
}
