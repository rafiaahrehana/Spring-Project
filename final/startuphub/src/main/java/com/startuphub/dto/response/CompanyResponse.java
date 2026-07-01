package com.startuphub.dto.response;

import com.startuphub.enums.CompanyStatus;
import com.startuphub.enums.SubscriptionPlan;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Full company response — returned to the company owner and SUPER_ADMIN.
 * Contains subscription details and internal status fields.
 */
public record CompanyResponse(
    Long id,
    String companyName,
    String subdomain,
    String companyEmail,
    String companyPhone,
    String website,
    String address,
    String logo,
    String primaryColor,
    String secondaryColor,
    String tagline,
    CompanyStatus status,
    SubscriptionPlan subscriptionPlan,
    LocalDate subscriptionStart,
    LocalDate subscriptionEnd,
    boolean trialExpired,
    Long ownerId,
    String ownerName,
    String ownerEmail,
    LocalDateTime createdAt
) {}
