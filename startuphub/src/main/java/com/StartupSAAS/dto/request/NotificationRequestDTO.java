package com.StartupSAAS.dto.request;

import com.StartupSAAS.enums.NotificationType;
import lombok.Data;

@Data
public class NotificationRequestDTO {
    private String title;
    private String message;
    private NotificationType type;
    private Long recipientId;
    private Long serviceRequestId;
    private Long companyId;
}
