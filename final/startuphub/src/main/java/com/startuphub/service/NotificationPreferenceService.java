package com.startuphub.service;

import com.startuphub.dto.request.UpdateNotificationPreferenceRequest;
import com.startuphub.dto.response.NotificationPreferenceResponse;

public interface NotificationPreferenceService {

    /** Get preferences for the authenticated user (auto-creates if missing) */
    NotificationPreferenceResponse getForCurrentUser();

    /** Full replacement update */
    NotificationPreferenceResponse update(UpdateNotificationPreferenceRequest request);

    /** Reset all preferences to platform defaults */
    NotificationPreferenceResponse resetToDefaults();

    /** Internal: create default preferences on user activation */
    void createDefaultsForUser(Long userId);
}
