package com.saas.luminex.service;

import com.saas.luminex.dto.response.ApiResponse;
import com.saas.luminex.entity.Notification;
import com.saas.luminex.entity.User;
import com.saas.luminex.enums.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationService {
    void send(User user, String title, String message, NotificationType type);
    Page<Notification> getMyNotifications(Pageable pageable);
    long getUnreadCount();
    void markAllAsRead();
    void markOneAsRead(Long id);
}
