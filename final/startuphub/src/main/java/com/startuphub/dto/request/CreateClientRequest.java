package com.startuphub.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateClientRequest(

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50)
    String firstName,

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50)
    String lastName,

    @NotBlank(message = "Email is required")
    @Email
    String email,

    @NotBlank(message = "Password is required")
    @Size(min = 8)
    String password,

    @Size(max = 30)
    String phone,

    @Size(max = 150)
    String clientCompanyName,

    @Size(max = 100)
    String industry,

    @Size(max = 255)
    String website,

    @Size(max = 50)
    String taxId,

    Long accountManagerId
) {}
