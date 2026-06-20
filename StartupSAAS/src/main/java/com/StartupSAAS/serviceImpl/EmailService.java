package com.StartupSAAS.serviceImpl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    public void sendSimpleMail(String to, String subject, String body) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(message, true);

        messageHelper.setFrom(fromEmail);
        messageHelper.setTo(to);
        messageHelper.setSubject(subject);
        messageHelper.setText(body, true);

        try {
            javaMailSender.send(message);
            log.info("Mail sent to {}: {}", to, subject);
        } catch (Exception e) {
            log.error("Failed to send mail to {}", to, e);
        }
    }

    // ── Email verification ───────────────────────────────────────
    public void sendVerificationEmail(String to, String name, String token) throws MessagingException {

        String link = frontendUrl + "/api/auth/verify-email?token=" + token;

        String body = """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                </head>
                <body style="margin:0; padding:0; background-color:#f1f5f9; font-family:Arial, sans-serif;">

                    <table width="100%%" cellpadding="0" cellspacing="0" style="padding:40px 0;">
                        <tr>
                            <td align="center">

                                <table width="600" cellpadding="0" cellspacing="0"
                                       style="background:#ffffff; border-radius:16px;
                                              overflow:hidden; box-shadow:0 4px 20px rgba(0,0,0,0.08);">

                                    <!-- Header -->
                                    <tr>
                                        <td align="center"
                                            style="background:linear-gradient(135deg,#2563eb,#1d4ed8);
                                                   padding:40px 20px;">
                                            <h1 style="color:#ffffff; margin:0;">
                                                StartupSAAS
                                            </h1>
                                            <p style="color:#dbeafe; margin-top:10px;">
                                                Business Operations, Simplified
                                            </p>
                                        </td>
                                    </tr>

                                    <!-- Content -->
                                    <tr>
                                        <td style="padding:40px; color:#334155;">

                                            <h2 style="margin-top:0; color:#0f172a;">
                                                Welcome, %s
                                            </h2>

                                            <p style="font-size:16px; line-height:1.7;">
                                                Thank you for registering with StartupSAAS.
                                                Please verify your email address to activate your account
                                                and start your 14-day free trial.
                                            </p>

                                            <div style="text-align:center; margin:35px 0;">
                                                <a href="%s"
                                                   style="display:inline-block;
                                                          background:#2563eb;
                                                          color:#ffffff;
                                                          text-decoration:none;
                                                          padding:14px 32px;
                                                          border-radius:8px;
                                                          font-size:16px;
                                                          font-weight:600;">
                                                    Verify Email
                                                </a>
                                            </div>

                                            <p style="font-size:14px; color:#64748b;">
                                                This verification link will expire in
                                                <strong>1 hour</strong>.
                                            </p>

                                            <p style="font-size:14px; color:#64748b;">
                                                If you did not create an account, you can safely ignore this email.
                                            </p>

                                        </td>
                                    </tr>

                                    <!-- Footer -->
                                    <tr>
                                        <td align="center"
                                            style="background:#f8fafc; padding:20px;
                                                   border-top:1px solid #e2e8f0;">

                                            <p style="margin:0; color:#64748b; font-size:13px;">
                                                © 2026 StartupSAAS. All rights reserved.
                                            </p>

                                            <p style="margin-top:8px; color:#94a3b8; font-size:12px;">
                                                This is an automated email. Please do not reply.
                                            </p>

                                        </td>
                                    </tr>

                                </table>

                            </td>
                        </tr>
                    </table>

                </body>
                </html>
                """.formatted(name, link);

        sendSimpleMail(to, "Verify your StartupSAAS account", body);
    }

    // ── Password reset ────────────────────────────────────────────
    public void sendPasswordResetEmail(String to, String name, String token) throws MessagingException {

        String link = frontendUrl + "/api/auth/reset-password?token=" + token;

        String body = """
                <div style="font-family: Arial, sans-serif; max-width: 500px; margin: auto;">
                    <h2 style="color:#0f172a;">Password Reset Request</h2>
                    <p>Hi %s, we received a request to reset your StartupSAAS password.</p>
                    <p style="margin: 24px 0;">
                        <a href="%s"
                           style="background:#ef4444; color:#fff; padding:12px 24px;
                                  border-radius:8px; text-decoration:none; font-weight:bold;">
                           Reset Password
                        </a>
                    </p>
                    <p style="color:#64748b; font-size: 13px;">
                        This link expires in 15 minutes. If you didn't request this,
                        you can safely ignore this email — your password will not change.
                    </p>
                </div>
                """.formatted(name, link);

        sendSimpleMail(to, "Reset your StartupSAAS password", body);
    }

    // ── Welcome (post-verification, trial started) ─────────────────
    public void sendWelcomeEmail(String to, String name, String companyName) throws MessagingException {

        String dashboardLink = frontendUrl + "/dashboard";

        String body = """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                </head>
                <body style="margin:0; padding:0; background-color:#f1f5f9; font-family:Arial, sans-serif;">

                    <table width="100%%" cellpadding="0" cellspacing="0" style="padding:40px 0;">
                        <tr>
                            <td align="center">

                                <table width="600" cellpadding="0" cellspacing="0"
                                       style="background:#ffffff; border-radius:16px;
                                              overflow:hidden; box-shadow:0 4px 20px rgba(0,0,0,0.08);">

                                    <tr>
                                        <td align="center"
                                            style="background:linear-gradient(135deg,#2563eb,#1d4ed8);
                                                   padding:40px 20px;">
                                            <h1 style="color:#ffffff; margin:0;">
                                                StartupSAAS
                                            </h1>
                                            <p style="color:#dbeafe; margin-top:10px;">
                                                Your account is ready, %s
                                            </p>
                                        </td>
                                    </tr>

                                    <tr>
                                        <td style="padding:40px; color:#334155;">

                                            <h2 style="margin-top:0; color:#0f172a;">
                                                Welcome to StartupSAAS, %s
                                            </h2>

                                            <p style="font-size:16px; line-height:1.7;">
                                                Your account for <strong>%s</strong> is now active.
                                                Your 14-day free trial has started — explore our full
                                                catalogue of services and get your business running smoother.
                                            </p>

                                            <div style="text-align:center; margin:35px 0;">
                                                <a href="%s"
                                                   style="display:inline-block;
                                                          background:#2563eb;
                                                          color:#ffffff;
                                                          text-decoration:none;
                                                          padding:14px 32px;
                                                          border-radius:8px;
                                                          font-size:16px;
                                                          font-weight:600;">
                                                    Go to Dashboard
                                                </a>
                                            </div>

                                            <p style="font-size:14px; color:#64748b;">
                                                Need help getting started? Check our knowledge base from
                                                inside your dashboard anytime.
                                            </p>

                                        </td>
                                    </tr>

                                    <tr>
                                        <td align="center"
                                            style="background:#f8fafc; padding:20px;
                                                   border-top:1px solid #e2e8f0;">

                                            <p style="margin:0; color:#64748b; font-size:13px;">
                                                © 2026 StartupSAAS. All rights reserved.
                                            </p>

                                            <p style="margin-top:8px; color:#94a3b8; font-size:12px;">
                                                This is an automated email. Please do not reply.
                                            </p>

                                        </td>
                                    </tr>

                                </table>

                            </td>
                        </tr>
                    </table>

                </body>
                </html>
                """.formatted(name, name, companyName, dashboardLink);

        sendSimpleMail(to, "Welcome to StartupSAAS — your trial has started", body);
    }

    // ── Trial expiring soon (e.g. 3 days left) ──────────────────────
    public void sendTrialExpiringEmail(String to, String name, String companyName, int daysLeft) throws MessagingException {

        String billingLink = frontendUrl + "/billing/subscribe";

        String body = """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                </head>
                <body style="margin:0; padding:0; background-color:#f1f5f9; font-family:Arial, sans-serif;">

                    <table width="100%%" cellpadding="0" cellspacing="0" style="padding:40px 0;">
                        <tr>
                            <td align="center">

                                <table width="600" cellpadding="0" cellspacing="0"
                                       style="background:#ffffff; border-radius:16px;
                                              overflow:hidden; box-shadow:0 4px 20px rgba(0,0,0,0.08);">

                                    <tr>
                                        <td align="center"
                                            style="background:linear-gradient(135deg,#f59e0b,#d97706);
                                                   padding:40px 20px;">
                                            <h1 style="color:#ffffff; margin:0;">
                                                StartupSAAS
                                            </h1>
                                            <p style="color:#fef3c7; margin-top:10px;">
                                                Your trial is ending soon
                                            </p>
                                        </td>
                                    </tr>

                                    <tr>
                                        <td style="padding:40px; color:#334155;">

                                            <h2 style="margin-top:0; color:#0f172a;">
                                                Hi %s,
                                            </h2>

                                            <p style="font-size:16px; line-height:1.7;">
                                                Your free trial for <strong>%s</strong> ends in
                                                <strong>%d day(s)</strong>. Subscribe now to keep
                                                uninterrupted access to all your services and data.
                                            </p>

                                            <div style="text-align:center; margin:35px 0;">
                                                <a href="%s"
                                                   style="display:inline-block;
                                                          background:#2563eb;
                                                          color:#ffffff;
                                                          text-decoration:none;
                                                          padding:14px 32px;
                                                          border-radius:8px;
                                                          font-size:16px;
                                                          font-weight:600;">
                                                    Choose a Plan
                                                </a>
                                            </div>

                                            <p style="font-size:14px; color:#64748b;">
                                                Questions about pricing? Just reply to your account
                                                manager or check our plans page.
                                            </p>

                                        </td>
                                    </tr>

                                    <tr>
                                        <td align="center"
                                            style="background:#f8fafc; padding:20px;
                                                   border-top:1px solid #e2e8f0;">

                                            <p style="margin:0; color:#64748b; font-size:13px;">
                                                © 2026 StartupSAAS. All rights reserved.
                                            </p>

                                            <p style="margin-top:8px; color:#94a3b8; font-size:12px;">
                                                This is an automated email. Please do not reply.
                                            </p>

                                        </td>
                                    </tr>

                                </table>

                            </td>
                        </tr>
                    </table>

                </body>
                </html>
                """.formatted(name, companyName, daysLeft, billingLink);

        String subject = "Your StartupSAAS trial ends in " + daysLeft + (daysLeft == 1 ? " day" : " days");
        sendSimpleMail(to, subject, body);
    }
}
