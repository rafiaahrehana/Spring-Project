package com.startuphub.dto.response;

import com.startuphub.enums.InvoiceStatus;
import com.startuphub.enums.InvoiceType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record InvoiceResponse(
    Long id,
    String invoiceNumber,
    InvoiceStatus status,
    InvoiceType type,
    BigDecimal subtotal,
    BigDecimal taxRate,
    BigDecimal taxAmount,
    BigDecimal discountAmount,
    BigDecimal totalAmount,
    BigDecimal paidAmount,
    BigDecimal outstandingAmount,
    LocalDate dueDate,
    LocalDateTime issuedAt,
    LocalDateTime paidAt,
    String notes,
    Long clientId,
    String clientName,
    Long serviceRequestId,
    String serviceRequestTitle,
    Long createdById,
    String createdByName,
    List<PaymentResponse> payments,
    LocalDateTime createdAt
) {}
