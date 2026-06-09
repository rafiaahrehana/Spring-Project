package com.saas.luminex.service;

import com.saas.luminex.dto.request.LoginRequest;
import com.saas.luminex.dto.request.RegisterRequest;
import com.saas.luminex.dto.response.AuthResponse;
import com.saas.luminex.entity.User;
import com.saas.luminex.enums.Role;
import com.saas.luminex.exception.BadRequestException;
import com.saas.luminex.repository.UserRepository;
import com.saas.luminex.security.JwtUtil;
import com.saas.luminex.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Unit Tests")
class AuthServiceImplTest {

    @Mock private AuthenticationManager authenticationManager;
    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtUtil jwtUtil;

    @InjectMocks
    private AuthServiceImpl authService;

    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = User.builder()
                .id(1L)
                .name("Test User")
                .email("test@luminex.com")
                .password("encoded_password")
                .role(Role.CLIENT)
                .isActive(true)
                .build();
    }

    // ─── Login ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("login() - success returns AuthResponse with token")
    void login_success() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@luminex.com");
        request.setPassword("password123");

        when(userRepository.findByEmail("test@luminex.com")).thenReturn(Optional.of(mockUser));
        when(jwtUtil.generateToken(mockUser)).thenReturn("mock.jwt.token");
        when(jwtUtil.generateRefreshToken(mockUser)).thenReturn("mock.refresh.token");

        AuthResponse response = authService.login(request);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("mock.jwt.token");
        assertThat(response.getEmail()).isEqualTo("test@luminex.com");
        assertThat(response.getRole()).isEqualTo(Role.CLIENT);
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    @DisplayName("login() - wrong credentials throws BadCredentialsException")
    void login_wrongCredentials_throws() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@luminex.com");
        request.setPassword("wrongpass");

        doThrow(new BadCredentialsException("Bad credentials"))
                .when(authenticationManager).authenticate(any());

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BadCredentialsException.class);
    }

    // ─── Register ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("register() - new email creates CLIENT user")
    void register_newEmail_createsClientUser() {
        RegisterRequest request = new RegisterRequest();
        request.setName("New User");
        request.setEmail("newuser@luminex.com");
        request.setPassword("password123");

        when(userRepository.existsByEmail("newuser@luminex.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encoded_password");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(jwtUtil.generateToken(any())).thenReturn("mock.jwt.token");
        when(jwtUtil.generateRefreshToken(any())).thenReturn("mock.refresh.token");

        AuthResponse response = authService.register(request);

        assertThat(response).isNotNull();
        verify(userRepository).save(argThat(user ->
                user.getRole() == Role.CLIENT &&
                user.getEmail().equals("newuser@luminex.com")
        ));
    }

    @Test
    @DisplayName("register() - duplicate email throws BadRequestException")
    void register_duplicateEmail_throws() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("existing@luminex.com");
        request.setPassword("password123");

        when(userRepository.existsByEmail("existing@luminex.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("already registered");
    }

    @Test
    @DisplayName("register() - self-registration always sets role to CLIENT")
    void register_roleAlwaysClient() {
        RegisterRequest request = new RegisterRequest();
        request.setName("Hacker");
        request.setEmail("hacker@test.com");
        request.setPassword("password123");

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        when(jwtUtil.generateToken(any())).thenReturn("token");
        when(jwtUtil.generateRefreshToken(any())).thenReturn("refresh");

        authService.register(request);

        verify(userRepository).save(argThat(user -> user.getRole() == Role.CLIENT));
    }

    // ─── Refresh Token ────────────────────────────────────────────────────────

    @Test
    @DisplayName("refreshToken() - valid token returns new AuthResponse")
    void refreshToken_valid_returnsNewTokens() {
        when(jwtUtil.validateToken("valid.refresh.token")).thenReturn(true);
        when(jwtUtil.extractEmail("valid.refresh.token")).thenReturn("test@luminex.com");
        when(userRepository.findByEmail("test@luminex.com")).thenReturn(Optional.of(mockUser));
        when(jwtUtil.generateToken(mockUser)).thenReturn("new.access.token");
        when(jwtUtil.generateRefreshToken(mockUser)).thenReturn("new.refresh.token");

        AuthResponse response = authService.refreshToken("valid.refresh.token");

        assertThat(response.getToken()).isEqualTo("new.access.token");
    }

    @Test
    @DisplayName("refreshToken() - expired token throws BadRequestException")
    void refreshToken_expired_throws() {
        when(jwtUtil.validateToken("expired.token")).thenReturn(false);

        assertThatThrownBy(() -> authService.refreshToken("expired.token"))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Invalid or expired");
    }
}
