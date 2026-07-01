package com.startuphub.dto.request;

import com.startuphub.enums.InvoiceType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateInvoiceRequest(

    @NotNull(message = "Client ID is required")
    Long clientId,

    Long serviceRequestId,

    InvoiceType type,

    @NotNull(message = "Subtotal is required")
    @DecimalMin(value = "0.00")
    BigDecimal subtotal,

    @DecimalMin(value = "0.00")
    BigDecimal taxRate,

    @DecimalMin(value = "0.00")
    BigDecimal discountAmount,

    LocalDate dueDate,

    String notes
) {}
