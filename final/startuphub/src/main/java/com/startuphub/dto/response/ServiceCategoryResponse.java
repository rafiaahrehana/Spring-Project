package com.startuphub.dto.response;

public record ServiceCategoryResponse(
    Long id,
    String name,
    String nameBn,
    String description,
    String iconUrl,
    int sortOrder,
    boolean active
) {}
