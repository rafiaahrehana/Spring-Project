package com.saas.luminex.dto.mapper;

import com.saas.luminex.dto.response.UserResponse;
import com.saas.luminex.entity.User;

public class UserMapper {

    public static UserResponse toDTO(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .companyName(user.getCompanyName())
                .address(user.getAddress())
                .phone(user.getPhone())
                .profileImageUrl(user.getProfileImageUrl())
                .isActive(user.isActive())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
