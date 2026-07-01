package com.startuphub.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreatePayrollRequest(

    @NotNull(message = "Employee ID is required")
    Long employeeId,

    @NotNull @Min(1) @Max(12)
    Integer payMonth,

    @NotNull @Min(2020)
    Integer payYear,

    @NotNull
    BigDecimal basicSalary,

    BigDecimal houseRent,

    BigDecimal medicalAllowance,

    BigDecimal transportAllowance,

    BigDecimal bonus,

    BigDecimal deductions,

    BigDecimal taxDeduction,

    String notes
) {}
