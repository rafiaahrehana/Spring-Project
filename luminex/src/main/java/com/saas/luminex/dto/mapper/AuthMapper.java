package com.saas.luminex.dto.mapper;

import com.saas.luminex.dto.response.AuthResponse;
import com.saas.luminex.entity.User;
import com.saas.luminex.security.JwtUtil;

public class AuthMapper {

    public static AuthResponse toDTO(User user, JwtUtil jwtUtil) {
        return AuthResponse.builder()
                .token(jwtUtil.generateToken(user))
                .refreshToken(jwtUtil.generateRefreshToken(user))
                .tokenType("Bearer")
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}
