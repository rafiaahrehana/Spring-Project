package com.saas.luminex.service.impl;

import com.saas.luminex.entity.Notification;
import com.saas.luminex.entity.User;
import com.saas.luminex.enums.NotificationType;
import com.saas.luminex.exception.ResourceNotFoundException;
import com.saas.luminex.repository.NotificationRepository;
import com.saas.luminex.service.NotificationService;
import com.saas.luminex.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final SecurityUtil securityUtil;

    @Override
    @Transactional
    public void send(User user, String title, String message, NotificationType type) {
        Notification notification = Notification.builder()
                .user(user)
                .title(title)
                .message(message)
                .type(type)
                .isRead(false)
                .build();
        notificationRepository.save(notification);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Notification> getMyNotifications(Pageable pageable) {
        User user = securityUtil.getCurrentUser();
        return notificationRepository.findByUserOrderByCreatedAtDesc(user, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount() {
        User user = securityUtil.getCurrentUser();
        return notificationRepository.countByUserAndIsReadFalse(user);
    }

    @Override
    @Transactional
    public void markAllAsRead() {
        User user = securityUtil.getCurrentUser();
        notificationRepository.markAllAsRead(user);
    }

    @Override
    @Transactional
    public void markOneAsRead(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", id));
        notification.setRead(true);
        notificationRepository.save(notification);
    }
}
