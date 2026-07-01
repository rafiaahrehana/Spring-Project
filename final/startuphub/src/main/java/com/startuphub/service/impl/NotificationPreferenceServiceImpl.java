package com.startuphub.service.impl;

import com.startuphub.dto.request.UpdateNotificationPreferenceRequest;
import com.startuphub.dto.response.NotificationPreferenceResponse;
import com.startuphub.entity.NotificationPreference;
import com.startuphub.entity.User;
import com.startuphub.exception.ResourceNotFoundException;
import com.startuphub.mapper.NotificationPreferenceMapper;
import com.startuphub.repository.NotificationPreferenceRepository;
import com.startuphub.repository.UserRepository;
import com.startuphub.security.SecurityUtil;
import com.startuphub.service.NotificationPreferenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationPreferenceServiceImpl implements NotificationPreferenceService {

    private final NotificationPreferenceRepository preferenceRepository;
    private final UserRepository                   userRepository;
    private final SecurityUtil                     securityUtil;

    @Override
    @Transactional
    public NotificationPreferenceResponse getForCurrentUser() {
        User user = securityUtil.getCurrentUser();
        NotificationPreference prefs = preferenceRepository.findByUserId(user.getId())
            .orElseGet(() -> createDefaults(user));
        return NotificationPreferenceMapper.toResponse(prefs);
    }

    @Override
    @Transactional
    public NotificationPreferenceResponse update(UpdateNotificationPreferenceRequest request) {
        User user = securityUtil.getCurrentUser();
        NotificationPreference prefs = preferenceRepository.findByUserId(user.getId())
            .orElseGet(() -> createDefaults(user));

        prefs.setEmailOnServiceRequest(request.emailOnServiceRequest());
        prefs.setEmailOnStatusChange(request.emailOnStatusChange());
        prefs.setEmailOnInvoice(request.emailOnInvoice());
        prefs.setEmailOnPayment(request.emailOnPayment());
        prefs.setEmailOnTaskAssigned(request.emailOnTaskAssigned());
        prefs.setEmailOnLeaveUpdate(request.emailOnLeaveUpdate());
        prefs.setInAppOnServiceRequest(request.inAppOnServiceRequest());
        prefs.setInAppOnStatusChange(request.inAppOnStatusChange());
        prefs.setEmailMarketing(request.emailMarketing());

        preferenceRepository.save(prefs);
        log.debug("Notification preferences updated for user={}", user.getEmail());
        return NotificationPreferenceMapper.toResponse(prefs);
    }

    @Override
    @Transactional
    public NotificationPreferenceResponse resetToDefaults() {
        User user = securityUtil.getCurrentUser();

        // Fix from Phase 2 review: use builder() so @Builder.Default values apply.
        // new NotificationPreference() sets all booleans to false (Java default),
        // bypassing @Builder.Default which sets them to true.
        NotificationPreference prefs = preferenceRepository.findByUserId(user.getId())
            .orElseGet(() -> NotificationPreference.builder().user(user).build());

        prefs.setUser(user);
        prefs.setEmailOnServiceRequest(true);
        prefs.setEmailOnStatusChange(true);
        prefs.setEmailOnInvoice(true);
        prefs.setEmailOnPayment(true);
        prefs.setEmailOnTaskAssigned(true);
        prefs.setEmailOnLeaveUpdate(true);
        prefs.setInAppOnServiceRequest(true);
        prefs.setInAppOnStatusChange(true);
        prefs.setEmailMarketing(false);

        preferenceRepository.save(prefs);
        log.debug("Notification preferences reset for user={}", user.getEmail());
        return NotificationPreferenceMapper.toResponse(prefs);
    }

    @Override
    @Transactional
    public void createDefaultsForUser(Long userId) {
        if (preferenceRepository.existsByUserId(userId)) {
            return;
        }
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
        createDefaults(user);
        log.debug("Default notification preferences created for userId={}", userId);
    }

    private NotificationPreference createDefaults(User user) {
        NotificationPreference prefs = NotificationPreference.builder()
            .user(user)
            .build();
        return preferenceRepository.save(prefs);
    }
}
