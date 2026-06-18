package com.StartupSAAS.dto.mapper;

import com.StartupSAAS.dto.response.NotificationResponseDTO;
import com.StartupSAAS.entity.Notification;
import com.StartupSAAS.entity.User;

public class NotificationMapper {

    public static NotificationResponseDTO toDTO(Notification n) {

        NotificationResponseDTO dto = new NotificationResponseDTO();
        dto.setId(n.getId());
        dto.setTitle(n.getTitle());
        dto.setMessage(n.getMessage());
        dto.setType(n.getType());
        dto.setIsRead(n.getIsRead());
        dto.setReadAt(n.getReadAt());
        dto.setCreatedAt(n.getCreatedAt());

        User recipient = n.getRecipient();
        if (recipient != null) {
            dto.setRecipientId(recipient.getId());
            dto.setRecipientName(recipient.getFirstName() + " " + recipient.getLastName());
            dto.setRecipientEmail(recipient.getEmail());
        }

        if (n.getServiceRequest() != null) {
            dto.setServiceRequestId(n.getServiceRequest().getId());
            dto.setServiceRequestTitle(n.getServiceRequest().getTitle());
        }

        return dto;
    }
}
