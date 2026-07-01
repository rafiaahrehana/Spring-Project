package com.startuphub.mapper;

import com.startuphub.dto.response.NotificationPreferenceResponse;
import com.startuphub.entity.NotificationPreference;

public final class NotificationPreferenceMapper {

    private NotificationPreferenceMapper() {}

    public static NotificationPreferenceResponse toResponse(NotificationPreference p) {
        return new NotificationPreferenceResponse(
            p.getId(),
            p.isEmailOnServiceRequest(),
            p.isEmailOnStatusChange(),
            p.isEmailOnInvoice(),
            p.isEmailOnPayment(),
            p.isEmailOnTaskAssigned(),
            p.isEmailOnLeaveUpdate(),
            p.isInAppOnServiceRequest(),
            p.isInAppOnStatusChange(),
            p.isEmailMarketing(),
            p.getUpdatedAt()
        );
    }
}
