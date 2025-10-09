package com.example.notifications_service.kafka;

import com.example.notifications_service.event.ClaimStatusChangedEvent;
import com.example.notifications_service.model.Notification;
import com.example.notifications_service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Kafka consumer to listen for claim status change events
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class ClaimEventConsumer {

    private final NotificationService notificationService;

    /**
     * Listen for claim status changed events from Kafka
     */
    @KafkaListener(
            topics = "${kafka.topic.claim-status-changed}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeClaimStatusChangedEvent(ClaimStatusChangedEvent event) {
        log.info("Received claim status change event: claimNumber={}, oldStatus={}, newStatus={}",
                event.getClaimNumber(), event.getOldStatus(), event.getNewStatus());

        try {
            // Create notification message based on the new status
            String message = buildNotificationMessage(event);
            Notification.NotificationType type = mapStatusToNotificationType(event.getNewStatus());

            // Create and save notification
            notificationService.createNotification(
                    event.getUserId(),
                    event.getClaimNumber(),
                    message,
                    type
            );

            log.info("Successfully created notification for claim: {}", event.getClaimNumber());
        } catch (Exception e) {
            log.error("Failed to process claim status change event for claim: {}",
                    event.getClaimNumber(), e);
        }
    }

    /**
     * Build a user-friendly notification message
     */
    private String buildNotificationMessage(ClaimStatusChangedEvent event) {
        String claimNumber = event.getClaimNumber();
        String newStatus = event.getNewStatus();

        return switch (newStatus) {
            case "APPROVED" -> String.format(
                    "Good news! Your claim %s has been approved and is ready for settlement.",
                    claimNumber
            );
            case "REJECTED" -> String.format(
                    "Your claim %s has been rejected. Reason: %s",
                    claimNumber,
                    event.getReason() != null ? event.getReason() : "Please contact support for details."
            );
            case "UNDER_REVIEW" -> String.format(
                    "Your claim %s is now under review by our team.",
                    claimNumber
            );
            case "SETTLED" -> String.format(
                    "Your claim %s has been settled. Payment processing initiated.",
                    claimNumber
            );
            default -> String.format(
                    "Status update for your claim %s: %s",
                    claimNumber,
                    newStatus
            );
        };
    }

    /**
     * Map claim status to notification type
     */
    private Notification.NotificationType mapStatusToNotificationType(String status) {
        return switch (status) {
            case "APPROVED" -> Notification.NotificationType.CLAIM_APPROVED;
            case "REJECTED" -> Notification.NotificationType.CLAIM_REJECTED;
            case "UNDER_REVIEW" -> Notification.NotificationType.CLAIM_UNDER_REVIEW;
            case "SETTLED" -> Notification.NotificationType.CLAIM_SETTLED;
            default -> Notification.NotificationType.SYSTEM_NOTIFICATION;
        };
    }
}
