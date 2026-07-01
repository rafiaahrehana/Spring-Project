package com.startuphub.dto.response;

import com.startuphub.enums.Role;

/**
 * Returned on successful login.
 * companyId is null for SUPER_ADMIN / SYSTEM_ADMIN users.
 */
public record LoginResponse(
    Long userId,
    String firstName,
    String email,
    Role role,
    Long companyId,
    String accessToken,
    String refreshToken
) {}
