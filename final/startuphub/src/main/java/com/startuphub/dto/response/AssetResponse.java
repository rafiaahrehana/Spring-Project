package com.startuphub.dto.response;

import com.startuphub.enums.AssetStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record AssetResponse(
    Long id,
    String name,
    String category,
    String serialNumber,
    String description,
    LocalDate purchaseDate,
    BigDecimal purchaseCost,
    AssetStatus status,
    LocalDate assignedAt,
    LocalDate returnDate,
    String notes,
    Long assignedToId,
    String assignedToName,
    LocalDateTime createdAt
) {}
