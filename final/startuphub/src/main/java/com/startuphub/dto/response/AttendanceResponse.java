package com.startuphub.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record AttendanceResponse(
    Long id,
    LocalDate date,
    LocalDateTime checkIn,
    LocalDateTime checkOut,
    boolean present,
    String notes,
    Long employeeId,
    String employeeName,
    LocalDateTime createdAt
) {}
