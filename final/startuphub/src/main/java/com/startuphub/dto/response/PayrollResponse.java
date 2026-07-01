package com.startuphub.dto.response;

import com.startuphub.enums.PayrollStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record PayrollResponse(
    Long id,
    int payMonth,
    int payYear,
    BigDecimal basicSalary,
    BigDecimal houseRent,
    BigDecimal medicalAllowance,
    BigDecimal transportAllowance,
    BigDecimal bonus,
    BigDecimal deductions,
    BigDecimal taxDeduction,
    BigDecimal netSalary,
    PayrollStatus status,
    String paymentReference,
    LocalDate paidAt,
    String notes,
    Long employeeId,
    String employeeName,
    Long approvedById,
    String approvedByName,
    LocalDateTime createdAt
) {}
