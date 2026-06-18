package com.StartupSAAS.service.impl;

import com.StartupSAAS.dto.mapper.NotificationMapper;
import com.StartupSAAS.dto.request.NotificationRequestDTO;
import com.StartupSAAS.dto.response.NotificationResponseDTO;
import com.StartupSAAS.entity.Company;
import com.StartupSAAS.entity.Notification;
import com.StartupSAAS.entity.ServiceRequest;
import com.StartupSAAS.entity.User;
import com.StartupSAAS.repository.CompanyRepository;
import com.StartupSAAS.repository.NotificationRepository;
import com.StartupSAAS.repository.ServiceRequestRepository;
import com.StartupSAAS.repository.UserRepository;
import com.StartupSAAS.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final ServiceRequestRepository serviceRequestRepository;
    private final CompanyRepository companyRepository;

    @Transactional
    @Override
    public NotificationResponseDTO create(NotificationRequestDTO dto) {

        User recipient = userRepository.findById(dto.getRecipientId())
                .orElseThrow(() -> new RuntimeException(
                        "User not found with id: " + dto.getRecipientId()));

        Notification notification = new Notification();
        notification.setTitle(dto.getTitle());
        notification.setMessage(dto.getMessage());
        notification.setType(dto.getType());
        notification.setRecipient(recipient);
        notification.setIsRead(false);

        if (dto.getServiceRequestId() != null) {
            ServiceRequest sr = serviceRequestRepository
                    .findById(dto.getServiceRequestId())
                    .orElseThrow(() -> new RuntimeException("ServiceRequest not found"));
            notification.setServiceRequest(sr);
        }

        if (dto.getCompanyId() != null) {
            Company company = companyRepository.findById(dto.getCompanyId())
                    .orElseThrow(() -> new RuntimeException("Company not found"));
            notification.setCompany(company);
        }

        Notification saved = notificationRepository.save(notification);
        return NotificationMapper.toDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponseDTO> getByUser(Long userId) {
        return notificationRepository.findByRecipientId(userId)
                .stream().map(NotificationMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponseDTO> getUnreadByUser(Long userId) {
        return notificationRepository.findByRecipientIdAndIsReadFalse(userId)
                .stream().map(NotificationMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public long countUnread(Long userId) {
        return notificationRepository.countByRecipientIdAndIsReadFalse(userId);
    }

    @Transactional
    @Override
    public NotificationResponseDTO markAsRead(Long id) {
        Notification n = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Notification not found with id: " + id));
        n.setIsRead(true);
        n.setReadAt(LocalDateTime.now());
        return NotificationMapper.toDTO(notificationRepository.save(n));
    }

    @Transactional
    @Override
    public void markAllAsRead(Long userId) {
        List<Notification> unread =
                notificationRepository.findByRecipientIdAndIsReadFalse(userId);
        unread.forEach(n -> {
            n.setIsRead(true);
            n.setReadAt(LocalDateTime.now());
        });
        notificationRepository.saveAll(unread);
    }

    @Override
    public void delete(Long id) {
        notificationRepository.deleteById(id);
    }
}
