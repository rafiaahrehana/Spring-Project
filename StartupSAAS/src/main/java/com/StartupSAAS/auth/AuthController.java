package com.StartupSAAS.auth;

import com.StartupSAAS.dto.request.CompanyRegisterRequest;
import com.StartupSAAS.dto.response.CompanyResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping(value = "/register-company", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CompanyResponse> register(
            @Valid @RequestPart("data") CompanyRegisterRequest request,
            @RequestPart(value = "logo", required = false) MultipartFile logo) {
        return new ResponseEntity<>(authService.registerCompany(request, logo), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}