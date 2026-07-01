package com.startuphub.mapper;

import com.startuphub.dto.response.UserResponse;
import com.startuphub.entity.User;

/**
 * Manual mapper — User entity to UserResponse DTO.
 * No MapStruct — explicit mapping for readability and debuggability.
 */
public final class UserMapper {

    private UserMapper() {}

    public static UserResponse toResponse(User user) {
        return new UserResponse(
            user.getId(),
            user.getFirstName(),
            user.getLastName(),
            user.getEmail(),
            user.getPhone(),
            user.getImage(),
            user.getRole(),
            user.isActive(),
            user.isEmailVerified(),
            user.isTwoFactorEnabled(),
            user.getLanguagePreference(),
            user.getCreatedAt()
        );
    }
}
