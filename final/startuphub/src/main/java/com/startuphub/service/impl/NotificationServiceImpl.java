package com.startuphub.service.impl;

import com.startuphub.dto.request.CreateNotificationRequest;
import com.startuphub.dto.response.NotificationCountResponse;
import com.startuphub.dto.response.NotificationResponse;
import com.startuphub.entity.Company;
import com.startuphub.entity.Notification;
import com.startuphub.entity.ServiceRequest;
import com.startuphub.entity.User;
import com.startuphub.exception.BadRequestException;
import com.startuphub.exception.ResourceNotFoundException;
import com.startuphub.mapper.NotificationMapper;
import com.startuphub.repository.NotificationRepository;
import com.startuphub.repository.UserRepository;
import com.startuphub.security.SecurityUtil;
import com.startuphub.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Notification dispatch service.
 *
 * Persistence-first model:
 *   1. Persist the Notification to the DB — guarantees delivery even
 *      if the WebSocket connection is not active.
 *   2. Push via WebSocket to /user/{userId}/queue/notifications.
 *   3. On WebSocket reconnect, Angular polls GET /api/notifications?unreadOnly=true
 *      to catch any notifications missed during disconnection.
 *
 * All send() calls are @Async — they never block the calling service thread.
 *
 * Deduplication: sendForServiceRequest() checks for an existing notification
 * of the same type + requestId + recipient before persisting, preventing
 * repeated SLA breach or assignment notifications.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository  notificationRepository;
    private final UserRepository          userRepository;
    private final SimpMessagingTemplate   messagingTemplate;
    private final SecurityUtil            securityUtil;

    @Override
    @Async
    @Transactional
    public void send(CreateNotificationRequest request) {
        User recipient = userRepository.findById(request.recipientId())
            .orElseThrow(() -> new ResourceNotFoundException(
                "Recipient not found: " + request.recipientId()));

        Notification notification = buildNotification(request, recipient);
        notificationRepository.save(notification);
        pushWebSocket(notification, recipient.getId());
    }

    @Override
    @Async
    @Transactional
    public void sendForServiceRequest(CreateNotificationRequest request) {
        if (request.serviceRequestId() != null
                && notificationRepository.existsByRecipientIdAndServiceRequestIdAndType(
                    request.recipientId(), request.serviceRequestId(), request.type())) {
            log.debug("Duplicate notification suppressed: type={} requestId={} recipientId={}",
                request.type(), request.serviceRequestId(), request.recipientId());
            return;
        }
        send(request);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationResponse> getMyNotifications(boolean unreadOnly, Pageable pageable) {
        Long userId = securityUtil.getCurrentUser().getId();
        Page<Notification> page = unreadOnly
            ? notificationRepository.findByRecipientIdAndReadFalseOrderByCreatedAtDesc(userId, pageable)
            : notificationRepository.findByRecipientIdOrderByCreatedAtDesc(userId, pageable);
        return page.map(NotificationMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public NotificationCountResponse getUnreadCount() {
        Long userId = securityUtil.getCurrentUser().getId();
        return new NotificationCountResponse(
            notificationRepository.countByRecipientIdAndReadFalse(userId));
    }

    @Override
    @Transactional
    public void markAsRead(Long notificationId) {
        Long userId = securityUtil.getCurrentUser().getId();
        Notification n = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new ResourceNotFoundException("Notification not found: " + notificationId));
        if (!n.getRecipient().getId().equals(userId)) {
            throw new BadRequestException("You can only mark your own notifications as read");
        }
        if (!n.isRead()) {
            n.setRead(true);
            n.setReadAt(LocalDateTime.now());
        }
    }

    @Override
    @Transactional
    public void markAllAsRead() {
        Long userId = securityUtil.getCurrentUser().getId();
        int count = notificationRepository.markAllReadForUser(userId, LocalDateTime.now());
        log.debug("Marked {} notifications as read for userId={}", count, userId);
    }

    // ── Private helpers ───────────────────────────────────────────

    private Notification buildNotification(CreateNotificationRequest request, User recipient) {
        ServiceRequest sr = null;
        if (request.serviceRequestId() != null) {
            sr = new ServiceRequest();
            sr.setId(request.serviceRequestId());
        }
        return Notification.builder()
            .type(request.type())
            .title(request.title())
            .message(request.message())
            .actionUrl(request.actionUrl())
            .recipient(recipient)
            .companyId(request.companyId())
            .serviceRequest(sr)
            .build();
    }

    private void pushWebSocket(Notification n, Long userId) {
        try {
            messagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/queue/notifications",
                NotificationMapper.toResponse(n)
            );
        } catch (Exception e) {
            // WebSocket push failure is non-critical — client will poll on reconnect
            log.debug("WebSocket push failed for userId={}: {}", userId, e.getMessage());
        }
    }
}
