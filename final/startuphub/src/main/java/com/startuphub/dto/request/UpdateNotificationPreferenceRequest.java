package com.startuphub.dto.request;

/**
 * Full replacement of notification preferences.
 * All fields are required — the entire preference record is replaced.
 * Angular should read the current state first then submit the updated state.
 */
public record UpdateNotificationPreferenceRequest(

    boolean emailOnServiceRequest,
    boolean emailOnStatusChange,
    boolean emailOnInvoice,
    boolean emailOnPayment,
    boolean emailOnTaskAssigned,
    boolean emailOnLeaveUpdate,
    boolean inAppOnServiceRequest,
    boolean inAppOnStatusChange,
    boolean emailMarketing

) {}
