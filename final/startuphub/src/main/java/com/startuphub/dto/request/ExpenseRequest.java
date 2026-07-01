package com.startuphub.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExpenseRequest(

    @NotBlank(message = "Title is required")
    @Size(max = 255)
    String title,

    @Size(max = 100)
    String category,

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01")
    BigDecimal amount,

    @NotNull(message = "Expense date is required")
    LocalDate expenseDate,

    String description,

    String receiptUrl
) {}
