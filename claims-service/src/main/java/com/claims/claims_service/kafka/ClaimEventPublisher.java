package com.claims.claims_service.kafka;

import com.claims.claims_service.event.ClaimStatusChangedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * Kafka producer service to publish claim status change events
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ClaimEventPublisher {

    private final KafkaTemplate<String, ClaimStatusChangedEvent> kafkaTemplate;

    @Value("${kafka.topic.claim-status-changed}")
    private String topic;

    /**
     * Publish a claim status changed event to Kafka
     */
    public void publishClaimStatusChanged(ClaimStatusChangedEvent event) {
        try {
            log.info("Publishing claim status change event: claimNumber={}, oldStatus={}, newStatus={}",
                    event.getClaimNumber(), event.getOldStatus(), event.getNewStatus());
            
            kafkaTemplate.send(topic, event.getClaimNumber(), event);
            
            log.info("Successfully published event for claim: {}", event.getClaimNumber());
        } catch (Exception e) {
            log.error("Failed to publish claim status change event for claim: {}",
                    event.getClaimNumber(), e);
        }
    }
}
