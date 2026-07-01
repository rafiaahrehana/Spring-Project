package com.startuphub.dto.response;

import com.startuphub.enums.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentResponse(
    Long id,
    BigDecimal amount,
    PaymentMethod paymentMethod,
    String paymentReference,
    LocalDateTime paidAt,
    String notes,
    Long invoiceId,
    String invoiceNumber,
    Long recordedById,
    String recordedByName,
    LocalDateTime createdAt
) {}
