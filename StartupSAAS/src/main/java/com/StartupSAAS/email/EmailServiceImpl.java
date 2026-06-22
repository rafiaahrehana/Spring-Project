package com.StartupSAAS.email;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final EmailTemplateBuilder templateBuilder;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    // ---------------- CORE ----------------

    @Async
    @Override
    public void sendSimpleMail(String to, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);

            mailSender.send(message);

            log.info("Email sent | to={} | subject={}", to, subject);

        } catch (Exception e) {
            log.error("Email failed | to={} | subject={}", to, subject, e);
        }
    }

    // ---------------- VERIFY EMAIL ----------------

    @Override
    public void sendVerificationEmail(String to, String name, String token) {

        String link = frontendUrl + "/verify-email?token=" + token;

        String content = """
            <div style="max-width:600px;margin:auto;background:#fff;padding:40px;border-radius:12px;">
                <h2>Welcome, %s</h2>
                <p>Please verify your account to activate your trial.</p>
                %s
                <p style="font-size:12px;color:#777;">Link expires in 1 hour</p>
            </div>
        """.formatted(
                name,
                templateBuilder.button("Verify Email", link, "#2563eb")
        );

        sendSimpleMail(to, EmailConstants.VERIFY_SUBJECT, templateBuilder.wrap(content));
    }

    // ---------------- RESET PASSWORD ----------------

    @Override
    public void sendPasswordResetEmail(String to, String name, String token) {

        String link = frontendUrl + "/reset-password?token=" + token;

        String content = """
            <div style="max-width:600px;margin:auto;background:#fff;padding:40px;border-radius:12px;">
                <h2>Password Reset</h2>
                <p>Hi %s, reset your password below:</p>
                %s
                <p style="font-size:12px;color:#777;">Expires in 15 minutes</p>
            </div>
        """.formatted(
                name,
                templateBuilder.button("Reset Password", link, "#ef4444")
        );

        sendSimpleMail(to, EmailConstants.RESET_SUBJECT, templateBuilder.wrap(content));
    }

    // ---------------- WELCOME ----------------

    @Override
    public void sendWelcomeEmail(String to, String name, String companyName) {

        String link = frontendUrl + "/dashboard";

        String content = """
            <div style="max-width:600px;margin:auto;background:#fff;padding:40px;border-radius:12px;">
                <h2>Welcome %s</h2>
                <p>Your company <b>%s</b> is now active.</p>
                %s
            </div>
        """.formatted(
                name,
                companyName,
                templateBuilder.button("Go to Dashboard", link, "#2563eb")
        );

        sendSimpleMail(to, EmailConstants.WELCOME_SUBJECT, templateBuilder.wrap(content));
    }

    // ---------------- TRIAL EXPIRY ----------------

    @Override
    public void sendTrialExpiringEmail(String to, String name, String companyName, int daysLeft) {

        String link = frontendUrl + "/billing/subscribe";

        String content = """
            <div style="max-width:600px;margin:auto;background:#fff;padding:40px;border-radius:12px;">
                <h2>Hi %s</h2>
                <p>Your trial for <b>%s</b> ends in <b>%d days</b>.</p>
                %s
            </div>
        """.formatted(
                name,
                companyName,
                daysLeft,
                templateBuilder.button("Choose Plan", link, "#f59e0b")
        );

        String subject = "Trial ends in " + daysLeft + (daysLeft == 1 ? " day" : " days");

        sendSimpleMail(to, subject, templateBuilder.wrap(content));
    }
}
