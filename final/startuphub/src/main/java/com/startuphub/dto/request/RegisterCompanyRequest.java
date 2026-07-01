package com.startuphub.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Used by SUPER_ADMIN to register a company directly (admin-side registration).
 * For self-registration by the company owner, use RegisterRequest in AuthController.
 */
public record RegisterCompanyRequest(

    @NotBlank(message = "Company name is required")
    @Size(min = 2, max = 150)
    String companyName,

    @NotBlank(message = "Subdomain is required")
    @Pattern(
        regexp = "^[a-z0-9]([a-z0-9-]{1,48}[a-z0-9])?$",
        message = "Subdomain: 3-50 chars, lowercase letters, numbers and hyphens only"
    )
    String subdomain,

    @NotBlank(message = "Owner first name is required")
    @Size(min = 2, max = 50)
    String ownerFirstName,

    @NotBlank(message = "Owner last name is required")
    @Size(min = 2, max = 50)
    String ownerLastName,

    @NotBlank(message = "Owner email is required")
    @Email(message = "Must be a valid email address")
    String ownerEmail,

    @NotBlank(message = "Owner password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    String ownerPassword,

    @Size(max = 30)
    String companyPhone

) {}
