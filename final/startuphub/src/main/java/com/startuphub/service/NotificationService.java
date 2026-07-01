package com.startuphub.service;

import com.startuphub.dto.request.CreateNotificationRequest;
import com.startuphub.dto.response.NotificationCountResponse;
import com.startuphub.dto.response.NotificationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationService {

    /**
     * Creates a persisted notification and pushes it via WebSocket.
     * @Async-friendly — callers fire and forget.
     */
    void send(CreateNotificationRequest request);

    /**
     * Convenience overload for service request notifications.
     * Skips duplicate check if an identical notification already exists.
     */
    void sendForServiceRequest(CreateNotificationRequest request);

    Page<NotificationResponse> getMyNotifications(boolean unreadOnly, Pageable pageable);

    NotificationCountResponse getUnreadCount();

    void markAsRead(Long notificationId);

    void markAllAsRead();
}
