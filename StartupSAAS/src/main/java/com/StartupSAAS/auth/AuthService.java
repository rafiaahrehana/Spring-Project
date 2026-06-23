package com.StartupSAAS.auth;

import com.StartupSAAS.dto.request.CompanyRegisterRequest;
import com.StartupSAAS.dto.response.CompanyResponse;
import org.springframework.web.multipart.MultipartFile;

public interface AuthService {
    CompanyResponse registerCompany(CompanyRegisterRequest request, MultipartFile logo);
    LoginResponse login(LoginRequest request);
    void resendVerificationEmail(String email);
    String verifyEmail(String token);
}