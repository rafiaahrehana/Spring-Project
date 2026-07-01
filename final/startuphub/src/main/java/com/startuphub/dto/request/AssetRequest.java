package com.startuphub.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AssetRequest(

    @NotBlank(message = "Asset name is required")
    @Size(max = 150)
    String name,

    @Size(max = 100)
    String category,

    @Size(max = 100)
    String serialNumber,

    String description,

    LocalDate purchaseDate,

    BigDecimal purchaseCost,

    Long assignedToId,

    String notes
) {}
