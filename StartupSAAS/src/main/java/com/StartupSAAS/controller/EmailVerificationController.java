package com.StartupSAAS.controller;

import com.StartupSAAS.entity.Company;
import com.StartupSAAS.exception.BadRequestException;
import com.StartupSAAS.repository.CompanyRepository;
import com.StartupSAAS.serviceImpl.EmailService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class EmailVerificationController {

    private final CompanyRepository companyRepository;
    private final EmailService emailService;

    @GetMapping("/verify-email")
    @Transactional
    public ResponseEntity<String> verifyEmail(@RequestParam String token) {

        Company company = companyRepository.findByVerificationToken(token)
                .orElseThrow(() -> new BadRequestException("Invalid or expired verification link"));

        if (company.isEmailVerified())
            return ResponseEntity.ok("Email already verified. You can log in.");

        if (company.getVerificationTokenExpiry() == null || company.getVerificationTokenExpiry().isBefore(LocalDateTime.now()))
            throw new BadRequestException("Verification link has expired. Please request a new one.");

        company.setEmailVerified(true);
        company.setVerificationToken(null);
        company.setVerificationTokenExpiry(null);

        try {
            emailService.sendWelcomeEmail(
                    company.getUser().getEmail(),
                    company.getUser().getFirstName(),
                    company.getCompanyName());
        } catch (MessagingException e) {
            log.error("Failed to send welcome email to {}", company.getUser().getEmail(), e);
        }

        return ResponseEntity.ok("Email verified successfully. Welcome to StartupSAAS!");
    }
}
