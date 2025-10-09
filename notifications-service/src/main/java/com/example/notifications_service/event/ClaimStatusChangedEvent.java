package com.example.notifications_service.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Event received from Kafka when a claim status changes
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClaimStatusChangedEvent {
    private String claimNumber;
    private String oldStatus;
    private String newStatus;
    private String userId;  // Keycloak user ID
    private String userEmail;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;
    
    private String changedBy;  // Admin who made the change
    private String reason;  // Reason for rejection (if applicable)
}
