package com.startuphub.dto.request;

import jakarta.validation.constraints.Size;

/**
 * Partial update for company profile fields.
 * All fields are optional — only non-null fields are applied.
 * Subdomain and owner cannot be changed through this endpoint.
 */
public record UpdateCompanyRequest(

    @Size(min = 2, max = 150, message = "Company name must be between 2 and 150 characters")
    String companyName,

    @Size(max = 30)
    String companyPhone,

    @Size(max = 255)
    String website,

    @Size(max = 255)
    String address,

    String logo,

    @Size(max = 7, message = "Primary color must be a valid hex code e.g. #2563eb")
    String primaryColor,

    @Size(max = 7)
    String secondaryColor,

    @Size(max = 255)
    String tagline

) {}
