package com.StartupSAAS.dto.response;

import com.StartupSAAS.enums.NotificationType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificationResponseDTO {
    private Long id;
    private String title;
    private String message;
    private NotificationType type;
    private Boolean isRead;
    private LocalDateTime readAt;
    private LocalDateTime createdAt;

    // Flattened recipient
    private Long recipientId;
    private String recipientName;
    private String recipientEmail;

    // Flattened service request (optional)
    private Long serviceRequestId;
    private String serviceRequestTitle;
}
