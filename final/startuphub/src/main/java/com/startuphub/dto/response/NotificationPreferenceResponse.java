package com.startuphub.dto.response;

import java.time.LocalDateTime;

public record NotificationPreferenceResponse(
    Long id,
    boolean emailOnServiceRequest,
    boolean emailOnStatusChange,
    boolean emailOnInvoice,
    boolean emailOnPayment,
    boolean emailOnTaskAssigned,
    boolean emailOnLeaveUpdate,
    boolean inAppOnServiceRequest,
    boolean inAppOnStatusChange,
    boolean emailMarketing,
    LocalDateTime updatedAt
) {}
