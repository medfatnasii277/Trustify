package com.claims.claims_service.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Event published when a claim status changes (Kafka message)
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
    private LocalDateTime timestamp;
    private String changedBy;  // Admin who made the change
    private String reason;  // Reason for rejection (if applicable)
}
