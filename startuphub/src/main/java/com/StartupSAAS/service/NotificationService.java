package com.StartupSAAS.service;

import com.StartupSAAS.dto.request.NotificationRequestDTO;
import com.StartupSAAS.dto.response.NotificationResponseDTO;

import java.util.List;

public interface NotificationService {

    NotificationResponseDTO create(NotificationRequestDTO dto);
    List<NotificationResponseDTO> getByUser(Long userId);
    List<NotificationResponseDTO> getUnreadByUser(Long userId);
    long countUnread(Long userId);
    NotificationResponseDTO markAsRead(Long id);
    void markAllAsRead(Long userId);
    void delete(Long id);
}
