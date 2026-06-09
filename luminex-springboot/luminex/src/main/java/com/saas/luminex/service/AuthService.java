package com.saas.luminex.service;

import com.saas.luminex.dto.request.LoginRequest;
import com.saas.luminex.dto.request.RegisterRequest;
import com.saas.luminex.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse login(LoginRequest request);
    AuthResponse register(RegisterRequest request);
    AuthResponse refreshToken(String refreshToken);
}
