package com.startuphub.dto.response;

import com.startuphub.enums.Role;

import java.time.LocalDateTime;

public record UserResponse(
    Long id,
    String firstName,
    String lastName,
    String email,
    String phone,
    String image,
    Role role,
    boolean active,
    boolean emailVerified,
    boolean twoFactorEnabled,
    String languagePreference,
    LocalDateTime createdAt
) {}
