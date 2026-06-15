package com.StartupSAAS.dto.response;

import com.StartupSAAS.dto.response.location.AddressResponse;
import com.StartupSAAS.enums.Role;
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
    private Long companyId;
    private String companyName;
    private AddressResponse address;
    private String phone;
    private String profileImageUrl;
    private boolean isActive;
    private LocalDateTime createdAt;
}
