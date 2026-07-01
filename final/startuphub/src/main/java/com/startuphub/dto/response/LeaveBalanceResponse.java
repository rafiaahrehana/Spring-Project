package com.startuphub.dto.response;

import com.startuphub.enums.LeaveType;

public record LeaveBalanceResponse(
    Long id,
    LeaveType leaveType,
    int year,
    int entitledDays,
    int usedDays,
    int pendingDays,
    int remainingDays
) {}
