package com.startuphub.dto.request;

import com.startuphub.enums.NotificationType;

/**
 * Internal DTO used by services to dispatch notifications.
 * Not exposed as an HTTP endpoint.
 */
public record CreateNotificationRequest(
    NotificationType type,
    String title,
    String message,
    String actionUrl,
    Long recipientId,
    Long companyId,
    Long serviceRequestId
) {
    public static CreateNotificationRequest of(
            NotificationType type, String title, String message,
            String actionUrl, Long recipientId, Long companyId) {
        return new CreateNotificationRequest(type, title, message, actionUrl, recipientId, companyId, null);
    }

    public static CreateNotificationRequest forRequest(
            NotificationType type, String title, String message,
            Long recipientId, Long companyId, Long serviceRequestId) {
        String url = "/service-requests/" + serviceRequestId;
        return new CreateNotificationRequest(type, title, message, url, recipientId, companyId, serviceRequestId);
    }
}
