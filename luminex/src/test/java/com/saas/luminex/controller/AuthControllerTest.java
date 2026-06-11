package com.saas.luminex.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saas.luminex.dto.request.LoginRequest;
import com.saas.luminex.dto.request.RegisterRequest;
import com.saas.luminex.dto.response.AuthResponse;
import com.saas.luminex.enums.Role;
import com.saas.luminex.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@DisplayName("AuthController Integration Tests")
class AuthControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private AuthService authService;

    private AuthResponse buildAuthResponse(Role role) {
        return AuthResponse.builder()
                .token("mock.jwt.token")
                .refreshToken("mock.refresh.token")
                .tokenType("Bearer")
                .userId(1L)
                .name("Test User")
                .email("test@luminex.com")
                .role(role)
                .build();
    }

    // ─── Login ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/auth/login - valid credentials returns 200 with token")
    void login_validCredentials_returns200() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@luminex.com");
        request.setPassword("password123");

        when(authService.login(any(LoginRequest.class))).thenReturn(buildAuthResponse(Role.CLIENT));

        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.token").value("mock.jwt.token"))
                .andExpect(jsonPath("$.data.role").value("CLIENT"))
                .andExpect(jsonPath("$.message").value("Login successful"));
    }

    @Test
    @DisplayName("POST /api/auth/login - blank email returns 400")
    void login_blankEmail_returns400() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("");
        request.setPassword("password123");

        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("POST /api/auth/login - invalid email format returns 400")
    void login_invalidEmailFormat_returns400() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("not-an-email");
        request.setPassword("password123");

        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    // ─── Register ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/auth/register - valid data returns 200 with token")
    void register_validData_returns200() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setName("New User");
        request.setEmail("newuser@luminex.com");
        request.setPassword("password123");

        when(authService.register(any(RegisterRequest.class))).thenReturn(buildAuthResponse(Role.CLIENT));

        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.role").value("CLIENT"))
                .andExpect(jsonPath("$.message").value("Registration successful"));
    }

    @Test
    @DisplayName("POST /api/auth/register - short password returns 400")
    void register_shortPassword_returns400() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setName("User");
        request.setEmail("user@test.com");
        request.setPassword("abc"); // less than 6 chars

        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data.password").exists());
    }

    @Test
    @DisplayName("POST /api/auth/register - missing name returns 400")
    void register_missingName_returns400() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("user@test.com");
        request.setPassword("password123");
        // name not set

        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data.name").exists());
    }
}
