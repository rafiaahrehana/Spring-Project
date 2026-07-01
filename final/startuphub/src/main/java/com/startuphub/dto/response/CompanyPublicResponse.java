package com.startuphub.dto.response;

/**
 * Minimal public company information.
 * Used when clients or employees need basic company details
 * without exposing subscription or owner information.
 */
public record CompanyPublicResponse(
    Long id,
    String companyName,
    String subdomain,
    String logo,
    String primaryColor,
    String secondaryColor,
    String tagline
) {}
