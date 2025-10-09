package com.example.notifications_service.repository;

import com.example.notifications_service.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Notification entity
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    /**
     * Find all notifications for a specific user
     */
    List<Notification> findByUserIdOrderByCreatedAtDesc(String userId);
    
    /**
     * Find unread notifications for a user
     */
    List<Notification> findByUserIdAndStatus(String userId, Notification.NotificationStatus status);
    
    /**
     * Count unread notifications for a user
     */
    long countByUserIdAndStatus(String userId, Notification.NotificationStatus status);
}
