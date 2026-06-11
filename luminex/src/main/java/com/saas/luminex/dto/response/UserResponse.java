package com.saas.luminex.dto.response;

import com.saas.luminex.enums.Role;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserResponse {
    private Long id;
    private String name;
    private String email;
    private Role role;
    private String companyName;
    private String address;
    private String phone;
    private String profileImageUrl;
    private boolean isActive;
    private LocalDateTime createdAt;
}
