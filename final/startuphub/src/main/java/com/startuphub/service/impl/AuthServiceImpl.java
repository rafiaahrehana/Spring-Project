package com.startuphub.service.impl;

import com.startuphub.dto.request.*;
import com.startuphub.dto.response.LoginResponse;
import com.startuphub.dto.response.TokenResponse;
import com.startuphub.dto.response.UserResponse;
import com.startuphub.entity.Company;
import com.startuphub.entity.Token;
import com.startuphub.entity.User;
import com.startuphub.enums.CompanyStatus;
import com.startuphub.enums.Role;
import com.startuphub.enums.SubscriptionPlan;
import com.startuphub.enums.TokenType;
import com.startuphub.exception.BadRequestException;
import com.startuphub.exception.ResourceNotFoundException;
import com.startuphub.mapper.UserMapper;
import com.startuphub.repository.ClientRepository;
import com.startuphub.repository.CompanyRepository;
import com.startuphub.repository.EmployeeRepository;
import com.startuphub.repository.TokenRepository;
import com.startuphub.repository.UserRepository;
import com.startuphub.security.JwtService;
import com.startuphub.service.AuditService;
import com.startuphub.service.AuthService;
import com.startuphub.service.EmailService;
import com.startuphub.service.NotificationPreferenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private static final long EMAIL_VERIFY_HOURS  = 24;
    private static final long PASSWORD_RESET_MINS = 15;

    private final UserRepository                 userRepository;
    private final CompanyRepository              companyRepository;
    private final EmployeeRepository             employeeRepository;
    private final ClientRepository               clientRepository;
    private final TokenRepository                tokenRepository;
    private final PasswordEncoder                passwordEncoder;
    private final JwtService                     jwtService;
    private final AuthenticationManager          authManager;
    private final EmailService                   emailService;
    private final AuditService                   auditService;
    private final NotificationPreferenceService  notificationPreferenceService;

    @Value("${jwt.refresh-expiration-ms:604800000}")
    private long refreshExpirationMs;

    @Value("${app.trial-days:14}")
    private int trialDays;

    @Override
    @Transactional
    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new BadRequestException("An account with this email already exists");
        }
        if (companyRepository.existsBySubdomain(request.subdomain())) {
            throw new BadRequestException("This subdomain is already taken");
        }

        User user = User.builder()
            .firstName(request.firstName())
            .lastName(request.lastName())
            .email(request.email().toLowerCase().trim())
            .password(passwordEncoder.encode(request.password()))
            .role(Role.COMPANY_OWNER)
            .active(false)
            .emailVerified(false)
            .build();
        userRepository.save(user);

        Company company = Company.builder()
            .companyName(request.companyName())
            .subdomain(request.subdomain().toLowerCase().trim())
            .companyPhone(request.companyPhone())
            .subscriptionPlan(SubscriptionPlan.FREE)
            .status(CompanyStatus.PENDING_VERIFICATION)
            .owner(user)
            .build();
        companyRepository.save(company);

        String verificationToken = createToken(user, TokenType.EMAIL_VERIFICATION,
            LocalDateTime.now().plusHours(EMAIL_VERIFY_HOURS));
        emailService.sendVerificationEmail(user.getEmail(), user.getFirstName(), verificationToken);

        log.info("Company registered: subdomain='{}' owner='{}'",
            company.getSubdomain(), user.getEmail());
        return UserMapper.toResponse(user);
    }

    @Override
    @Transactional
    public LoginResponse login(LoginRequest request) {
        authManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.email(), request.password()));

        User user = userRepository.findByEmail(request.email())
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Long companyId = resolveCompanyId(user);

        String accessToken  = jwtService.generateAccessToken(
            user.getEmail(), user.getRole().name(), companyId);
        String refreshToken = jwtService.generateRefreshToken(user.getEmail());

        revokeAllRefreshTokens(user);
        persistToken(user, refreshToken, TokenType.REFRESH,
            LocalDateTime.now().plusSeconds(refreshExpirationMs / 1000));

        auditService.logLogin(user, companyId);
        log.info("Login: email='{}' role='{}' companyId={}",
            user.getEmail(), user.getRole(), companyId);

        return new LoginResponse(user.getId(), user.getFirstName(), user.getEmail(),
            user.getRole(), companyId, accessToken, refreshToken);
    }

    @Override
    @Transactional
    public TokenResponse refreshToken(RefreshTokenRequest request) {
        Token stored = tokenRepository
            .findByTokenAndType(request.refreshToken(), TokenType.REFRESH)
            .orElseThrow(() -> new BadRequestException("Invalid refresh token"));

        if (!stored.isValid()) {
            throw new BadRequestException(
                "Refresh token has expired or been revoked. Please log in again.");
        }

        User user = stored.getUser();
        stored.setRevoked(true);

        Long companyId = resolveCompanyId(user);
        String newAccess  = jwtService.generateAccessToken(
            user.getEmail(), user.getRole().name(), companyId);
        String newRefresh = jwtService.generateRefreshToken(user.getEmail());

        persistToken(user, newRefresh, TokenType.REFRESH,
            LocalDateTime.now().plusSeconds(refreshExpirationMs / 1000));

        return new TokenResponse(newAccess, newRefresh);
    }

    @Override
    @Transactional
    public void logout(RefreshTokenRequest request) {
        tokenRepository.findByTokenAndType(request.refreshToken(), TokenType.REFRESH)
            .ifPresent(token -> {
                Long companyId = resolveCompanyId(token.getUser());
                auditService.logLogout(token.getUser(), companyId);
                token.setRevoked(true);
            });
    }

    @Override
    @Transactional
    public void verifyEmail(VerifyEmailRequest request) {
        Token token = tokenRepository
            .findByTokenAndType(request.token(), TokenType.EMAIL_VERIFICATION)
            .orElseThrow(() -> new BadRequestException("Invalid or expired verification link"));

        if (token.isUsed()) throw new BadRequestException("This verification link has already been used");
        if (token.isExpired()) throw new BadRequestException("Verification link has expired. Please request a new one.");

        User user = token.getUser();
        user.setActive(true);
        user.setEmailVerified(true);
        userRepository.save(user);

        token.setUsed(true);
        tokenRepository.save(token);

        companyRepository.findByOwnerId(user.getId()).ifPresent(company -> {
            company.setStatus(CompanyStatus.TRIAL);
            company.setSubscriptionStart(LocalDate.now());
            company.setSubscriptionEnd(LocalDate.now().plusDays(trialDays));
            companyRepository.save(company);
            notificationPreferenceService.createDefaultsForUser(user.getId());
            emailService.sendWelcomeEmail(user.getEmail(), user.getFirstName(), company.getCompanyName());
            log.info("Company activated: id={} subdomain='{}' trialEnds={}",
                company.getId(), company.getSubdomain(), company.getSubscriptionEnd());
        });
    }

    @Override
    @Transactional
    public void resendVerification(ResendVerificationRequest request) {
        User user = userRepository.findByEmail(request.email())
            .orElseThrow(() -> new ResourceNotFoundException("No account found with that email"));

        if (user.isEmailVerified()) throw new BadRequestException("This email is already verified");

        tokenRepository.deleteByUserIdAndType(user.getId(), TokenType.EMAIL_VERIFICATION);
        String newToken = createToken(user, TokenType.EMAIL_VERIFICATION,
            LocalDateTime.now().plusHours(EMAIL_VERIFY_HOURS));
        emailService.sendVerificationEmail(user.getEmail(), user.getFirstName(), newToken);
        log.info("Verification email resent to: {}", user.getEmail());
    }

    @Override
    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        userRepository.findByEmail(request.email()).ifPresent(user -> {
            tokenRepository.deleteByUserIdAndType(user.getId(), TokenType.PASSWORD_RESET);
            String resetToken = createToken(user, TokenType.PASSWORD_RESET,
                LocalDateTime.now().plusMinutes(PASSWORD_RESET_MINS));
            emailService.sendPasswordResetEmail(user.getEmail(), user.getFirstName(), resetToken);
            log.info("Password reset email sent to: {}", user.getEmail());
        });
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        if (!request.newPassword().equals(request.confirmPassword())) {
            throw new BadRequestException("Passwords do not match");
        }

        Token token = tokenRepository
            .findByTokenAndType(request.token(), TokenType.PASSWORD_RESET)
            .orElseThrow(() -> new BadRequestException("Invalid or expired reset link"));

        if (token.isUsed()) throw new BadRequestException("This reset link has already been used");
        if (token.isExpired()) throw new BadRequestException("Reset link has expired. Please request a new one.");

        User user = token.getUser();
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);

        token.setUsed(true);
        tokenRepository.save(token);
        revokeAllRefreshTokens(user);
        log.info("Password reset for: {}", user.getEmail());
    }

    // ── Private helpers ───────────────────────────────────────────

    /**
     * Phase 3: resolves companyId for all tenant roles.
     *   COMPANY_OWNER → Company.owner_id lookup
     *   EMPLOYEE      → Employee.user_id lookup (added in Phase 3)
     *   CLIENT        → Client.user_id lookup   (added in Phase 3)
     *   ADMIN         → Employee.user_id lookup (ADMINs are employees)
     *   Platform roles → null (no company scope)
     */
    private Long resolveCompanyId(User user) {
        return switch (user.getRole()) {
            case SUPER_ADMIN, SYSTEM_ADMIN -> null;
            case COMPANY_OWNER -> companyRepository.findByOwnerId(user.getId())
                .map(Company::getId).orElse(null);
            case EMPLOYEE, ADMIN -> employeeRepository.findCompanyIdByUserId(user.getId())
                .orElse(null);
            case CLIENT -> clientRepository.findCompanyIdByUserId(user.getId())
                .orElse(null);
        };
    }

    private String createToken(User user, TokenType type, LocalDateTime expiresAt) {
        String value = UUID.randomUUID().toString();
        persistToken(user, value, type, expiresAt);
        return value;
    }

    private void persistToken(User user, String value, TokenType type, LocalDateTime expiresAt) {
        tokenRepository.save(Token.builder()
            .token(value).type(type).user(user).expiresAt(expiresAt).build());
    }

    private void revokeAllRefreshTokens(User user) {
        tokenRepository.revokeAllByUserIdAndType(user.getId(), TokenType.REFRESH);
    }
}
