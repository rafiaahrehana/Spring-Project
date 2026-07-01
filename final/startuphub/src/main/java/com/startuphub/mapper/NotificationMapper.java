package com.startuphub.mapper;

import com.startuphub.dto.response.NotificationResponse;
import com.startuphub.entity.Notification;
import com.startuphub.entity.ServiceRequest;

public final class NotificationMapper {

    private NotificationMapper() {}

    public static NotificationResponse toResponse(Notification n) {
        ServiceRequest sr = n.getServiceRequest();
        return new NotificationResponse(
            n.getId(),
            n.getType(),
            n.getTitle(),
            n.getMessage(),
            n.getActionUrl(),
            n.isRead(),
            n.getReadAt(),
            sr != null ? sr.getId() : null,
            n.getCreatedAt()
        );
    }
}
