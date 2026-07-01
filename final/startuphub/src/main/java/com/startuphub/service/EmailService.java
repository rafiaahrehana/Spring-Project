package com.startuphub.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

/**
 * Sends transactional emails asynchronously.
 *
 * All methods are @Async — email delivery never blocks the HTTP response.
 * Failures are logged and absorbed — they do not propagate to the caller.
 *
 * Phase 2 changes:
 *   sendWelcomeEmail() now accepts companyName to personalise the message.
 *   sendTrialExpiringEmail() added for the subscription scheduler.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:noreply@startuphub.com}")
    private String fromAddress;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    // ── Auth flows ────────────────────────────────────────────────

    @Async
    public void sendVerificationEmail(String to, String firstName, String token) {
        String link = frontendUrl + "/auth/verify-email?token=" + token;
        String html = buildEmail(
            "Hi " + firstName + ",",
            "You're almost there! Please verify your email address to activate your account.",
            "Verify Email Address", link,
            "This link expires in 24 hours. If you did not create an account, please ignore this email."
        );
        send(to, "Verify your StartupHub account", html);
    }

    @Async
    public void sendPasswordResetEmail(String to, String firstName, String token) {
        String link = frontendUrl + "/auth/reset-password?token=" + token;
        String html = buildEmail(
            "Hi " + firstName + ",",
            "We received a request to reset your password.",
            "Reset Password", link,
            "This link expires in 15 minutes. If you did not request this, you can safely ignore this email."
        );
        send(to, "Reset your StartupHub password", html);
    }

    /**
     * Sent after successful email verification.
     * Phase 2: companyName included in the message.
     */
    @Async
    public void sendWelcomeEmail(String to, String firstName, String companyName) {
        String html = buildEmail(
            "Welcome, " + firstName + "!",
            "<strong>" + companyName + "</strong> is now active on StartupHub. "
                + "Your 14-day free trial has started. Explore the platform and set up your services.",
            "Go to Dashboard", frontendUrl + "/dashboard",
            "Need help getting started? Visit our documentation at docs.startuphub.com"
        );
        send(to, "Welcome to StartupHub — " + companyName + " is live!", html);
    }

    // ── Subscription flows ────────────────────────────────────────

    @Async
    public void sendTrialExpiringEmail(String to, String firstName,
                                        String companyName, int daysLeft) {
        String message = daysLeft == 0
            ? "Your free trial for <strong>" + companyName + "</strong> expires today."
            : "Your free trial for <strong>" + companyName + "</strong> expires in "
                + daysLeft + " day" + (daysLeft == 1 ? "" : "s") + ".";
        String html = buildEmail(
            "Hi " + firstName + ",",
            message + " Upgrade to a paid plan to keep all features active.",
            "Upgrade Now", frontendUrl + "/settings/billing",
            "If you have questions about our plans, contact us at support@startuphub.com"
        );
        send(to, "Your StartupHub trial expires in " + daysLeft + " day(s)", html);
    }

    @Async
    public void sendSubscriptionSuspendedEmail(String to, String firstName, String companyName) {
        String html = buildEmail(
            "Hi " + firstName + ",",
            "Your account for <strong>" + companyName + "</strong> has been suspended "
                + "because your subscription has expired. Your data is safe.",
            "Reactivate Now", frontendUrl + "/settings/billing",
            "Need assistance? Contact us at support@startuphub.com"
        );
        send(to, "Your StartupHub account has been suspended — " + companyName, html);
    }

    // ── Private helpers ───────────────────────────────────────────

    private void send(String to, String subject, String htmlBody) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromAddress, "StartupHub");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            mailSender.send(message);
            log.info("Email sent: subject='{}' to='{}'", subject, to);
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage(), e);
        }
    }

    private String buildEmail(String greeting, String body,
                               String ctaText, String ctaUrl, String footer) {
        return "<!DOCTYPE html><html><body style='font-family:Arial,sans-serif;"
            + "color:#1e293b;max-width:600px;margin:0 auto;padding:20px'>"
            + "<div style='background:#1e3a5f;padding:20px;border-radius:8px 8px 0 0'>"
            + "<h1 style='color:white;margin:0;font-size:24px'>StartupHub</h1></div>"
            + "<div style='background:#f8fafc;padding:30px;border-radius:0 0 8px 8px;"
            + "border:1px solid #e2e8f0'>"
            + "<p style='font-size:18px;font-weight:bold'>" + greeting + "</p>"
            + "<p>" + body + "</p>"
            + "<a href='" + ctaUrl + "' style='display:inline-block;background:#2563eb;"
            + "color:white;padding:12px 24px;border-radius:6px;text-decoration:none;"
            + "font-weight:bold;margin:16px 0'>" + ctaText + "</a>"
            + "<p style='color:#64748b;font-size:13px;margin-top:20px'>" + footer + "</p>"
            + "<hr style='border:none;border-top:1px solid #e2e8f0;margin:20px 0'>"
            + "<p style='color:#94a3b8;font-size:11px'>© 2025 StartupHub. All rights reserved.</p>"
            + "</div></body></html>";
    }
}
