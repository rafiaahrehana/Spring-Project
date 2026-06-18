package com.StartupSAAS.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponse {
    private String token;
    private Long userId;
    private String email;
    private String role;
}