package com.StartupSAAS.auth;

import com.StartupSAAS.dto.request.CompanyRegisterRequest;
import com.StartupSAAS.dto.request.LoginRequest;
import com.StartupSAAS.dto.response.CompanyResponse;
import com.StartupSAAS.dto.response.LoginResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register-company")
    public ResponseEntity<CompanyResponse> register(
            @Valid @RequestBody CompanyRegisterRequest request) {
        return new ResponseEntity<>(authService.registerCompany(request), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}