package com.example.notifications_service.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.example.notifications_service.model.Notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service for sending real-time notifications via WebSocket
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WebSocketNotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Send a notification to a specific user via WebSocket
     * The notification will be sent to the user's personal queue: /user/{userId}/notifications
     * 
     * @param userId The Keycloak user ID
     * @param notification The notification object to send
     */
    public void sendNotificationToUser(String userId, Notification notification) {
        try {
            String destination = "/user/" + userId + "/notifications";
            log.info("Sending WebSocket notification to user {} at destination {}", userId, destination);
            
            messagingTemplate.convertAndSend(destination, notification);
            
            log.info("Successfully sent WebSocket notification to user {}", userId);
        } catch (Exception e) {
            log.error("Error sending WebSocket notification to user {}: {}", userId, e.getMessage(), e);
        }
    }

    /**
     * Broadcast a notification to all connected users
     * 
     * @param notification The notification object to broadcast
     */
    public void broadcastNotification(Notification notification) {
        try {
            String destination = "/topic/notifications";
            log.info("Broadcasting WebSocket notification to all users");
            
            messagingTemplate.convertAndSend(destination, notification);
            
            log.info("Successfully broadcast WebSocket notification");
        } catch (Exception e) {
            log.error("Error broadcasting WebSocket notification: {}", e.getMessage(), e);
        }
    }
}
