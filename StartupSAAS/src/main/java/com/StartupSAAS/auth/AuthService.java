package com.StartupSAAS.auth;


import com.StartupSAAS.dto.request.CompanyRegisterRequest;
import com.StartupSAAS.dto.request.LoginRequest;
import com.StartupSAAS.dto.response.CompanyResponse;
import com.StartupSAAS.dto.response.LoginResponse;

public interface AuthService {
    CompanyResponse registerCompany(CompanyRegisterRequest request);
    LoginResponse login(LoginRequest request);
}