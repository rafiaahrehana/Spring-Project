package com.startuphub.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record AttendanceRequest(

    @NotNull(message = "Date is required")
    LocalDate date,

    LocalDateTime checkIn,

    LocalDateTime checkOut,

    String notes,

    boolean present
) {}
