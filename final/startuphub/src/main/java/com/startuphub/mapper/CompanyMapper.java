package com.startuphub.mapper;

import com.startuphub.dto.response.CompanyPublicResponse;
import com.startuphub.dto.response.CompanyResponse;
import com.startuphub.entity.Company;
import com.startuphub.entity.User;

/**
 * Manual mapper — Company entity to response DTOs.
 */
public final class CompanyMapper {

    private CompanyMapper() {}

    public static CompanyResponse toResponse(Company c) {
        User owner = c.getOwner();
        return new CompanyResponse(
            c.getId(),
            c.getCompanyName(),
            c.getSubdomain(),
            c.getCompanyEmail(),
            c.getCompanyPhone(),
            c.getWebsite(),
            c.getAddress(),
            c.getLogo(),
            c.getPrimaryColor(),
            c.getSecondaryColor(),
            c.getTagline(),
            c.getStatus(),
            c.getSubscriptionPlan(),
            c.getSubscriptionStart(),
            c.getSubscriptionEnd(),
            c.isTrialExpired(),
            owner != null ? owner.getId()       : null,
            owner != null ? owner.getFullName() : null,
            owner != null ? owner.getEmail()    : null,
            c.getCreatedAt()
        );
    }

    public static CompanyPublicResponse toPublicResponse(Company c) {
        return new CompanyPublicResponse(
            c.getId(),
            c.getCompanyName(),
            c.getSubdomain(),
            c.getLogo(),
            c.getPrimaryColor(),
            c.getSecondaryColor(),
            c.getTagline()
        );
    }
}
