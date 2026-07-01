package com.startuphub.dto.response;

import com.startuphub.enums.ExpenseStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ExpenseResponse(
    Long id,
    String title,
    String category,
    BigDecimal amount,
    LocalDate expenseDate,
    String description,
    String receiptUrl,
    ExpenseStatus status,
    String rejectionReason,
    LocalDateTime reimbursedAt,
    Long submittedById,
    String submittedByName,
    Long approvedById,
    String approvedByName,
    LocalDateTime createdAt
) {}
