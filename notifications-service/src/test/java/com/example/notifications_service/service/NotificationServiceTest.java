package com.example.notifications_service.service;

import com.example.notifications_service.model.Notification;
import com.example.notifications_service.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetUserNotifications() {
        String userId = "user123";
        Notification notif1 = new Notification();
        notif1.setUserId(userId);
        Notification notif2 = new Notification();
        notif2.setUserId(userId);
        List<Notification> notifications = Arrays.asList(notif1, notif2);
        when(notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)).thenReturn(notifications);

        List<Notification> result = notificationService.getUserNotifications(userId);
        assertEquals(2, result.size());
        verify(notificationRepository, times(1)).findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Test
    void testGetUnreadNotifications() {
        String userId = "user123";
        Notification notif = new Notification();
        notif.setUserId(userId);
        notif.setStatus(Notification.NotificationStatus.UNREAD);
        List<Notification> notifications = Collections.singletonList(notif);
        when(notificationRepository.findByUserIdAndStatus(userId, Notification.NotificationStatus.UNREAD)).thenReturn(notifications);

        List<Notification> result = notificationService.getUnreadNotifications(userId);
        assertEquals(1, result.size());
        assertEquals(Notification.NotificationStatus.UNREAD, result.get(0).getStatus());
        verify(notificationRepository, times(1)).findByUserIdAndStatus(userId, Notification.NotificationStatus.UNREAD);
    }

    @Test
    void testGetUnreadCount() {
        String userId = "user123";
        when(notificationRepository.countByUserIdAndStatus(userId, Notification.NotificationStatus.UNREAD)).thenReturn(5L);
        long count = notificationService.getUnreadCount(userId);
        assertEquals(5L, count);
        verify(notificationRepository, times(1)).countByUserIdAndStatus(userId, Notification.NotificationStatus.UNREAD);
    }
}
