package com.saas.luminex.service.impl;

import com.saas.luminex.dto.mapper.AuthMapper;
import com.saas.luminex.dto.request.LoginRequest;
import com.saas.luminex.dto.request.RegisterRequest;
import com.saas.luminex.dto.response.AuthResponse;
import com.saas.luminex.entity.User;
import com.saas.luminex.enums.Role;
import com.saas.luminex.exception.BadRequestException;
import com.saas.luminex.repository.UserRepository;
import com.saas.luminex.security.JwtUtil;
import com.saas.luminex.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("User not found"));

        log.info("User logged in: {} [{}]", user.getEmail(), user.getRole());

        return AuthMapper.toDTO(user, jwtUtil);
    }

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email is already registered");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.CLIENT)
                .companyName(request.getCompanyName())
                .address(request.getAddress())
                .phone(request.getPhone())
                .isActive(true)
                .build();

        userRepository.save(user);
        log.info("New client registered: {}", user.getEmail());

        return AuthMapper.toDTO(user, jwtUtil);
    }

    @Override
    public AuthResponse refreshToken(String refreshToken) {
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new BadRequestException("Invalid or expired refresh token");
        }
        String email = jwtUtil.extractEmail(refreshToken);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("User not found"));
        return AuthMapper.toDTO(user, jwtUtil);
    }
}
