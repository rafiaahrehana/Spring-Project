package com.startuphub.dto.response;

import com.startuphub.enums.ClientStatus;

import java.time.LocalDateTime;

public record ClientResponse(
    Long id,
    Long userId,
    String firstName,
    String lastName,
    String email,
    String phone,
    String image,
    String clientCompanyName,
    String industry,
    String website,
    ClientStatus status,
    boolean portalAccessEnabled,
    Long accountManagerId,
    String accountManagerName,
    LocalDateTime onboardedAt,
    LocalDateTime createdAt
) {}
