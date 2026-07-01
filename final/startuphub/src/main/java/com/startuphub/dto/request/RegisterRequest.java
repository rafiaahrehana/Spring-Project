package com.startuphub.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Company owner registration request.
 *
 * Phase 2 additions over Phase 1:
 *   companyName — the tenant's display name
 *   subdomain   — globally unique identifier for the tenant
 *   companyPhone — optional contact number
 *
 * Subdomain rules:
 *   - 3–50 characters
 *   - lowercase letters, numbers, hyphens only
 *   - cannot start or end with a hyphen
 *   Valid:   my-company, techfirm99, abc
 *   Invalid: -company, company-, My_Company, a
 */
public record RegisterRequest(

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    String firstName,

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    String lastName,

    @NotBlank(message = "Email is required")
    @Email(message = "Must be a valid email address")
    String email,

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    String password,

    @NotBlank(message = "Company name is required")
    @Size(min = 2, max = 150, message = "Company name must be between 2 and 150 characters")
    String companyName,

    @NotBlank(message = "Subdomain is required")
    @Pattern(
        regexp = "^[a-z0-9]([a-z0-9-]{1,48}[a-z0-9])?$",
        message = "Subdomain must be 3–50 characters, lowercase letters, numbers and hyphens only, cannot start or end with a hyphen"
    )
    String subdomain,

    @Size(max = 30, message = "Phone number must not exceed 30 characters")
    String companyPhone

) {}
