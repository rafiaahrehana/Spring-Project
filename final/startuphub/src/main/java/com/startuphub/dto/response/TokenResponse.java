package com.startuphub.dto.response;

public record TokenResponse(
    String accessToken,
    String refreshToken
) {}
