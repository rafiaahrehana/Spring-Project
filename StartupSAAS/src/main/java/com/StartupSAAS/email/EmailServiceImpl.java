package com.StartupSAAS.email;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final EmailTemplate template;

    @Value("${spring.mail.username}")
    private String from;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Async
    @Override
    public void send(String to, String subject, String html) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);
            mailSender.send(message);
            log.info("Email sent to {}", to);
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage(), e);
        }
    }

    @Async
    @Override
    public void sendVerificationEmail(String to, String name, String token, EmailBranding.Data branding) {
        String link = frontendUrl + "/verify-email?token=" + token;
        String html = template.build(branding, "", "Welcome " + name + ", please verify your email.", "Verify Email", link);
        send(to, "Verify your StartupHub account", html);
    }

    @Async
    @Override
    public void sendWelcomeEmail(String to, String name, EmailBranding.Data branding) {
        String html = template.build(branding, "", "Your company account is ready.", "Open Dashboard", frontendUrl + "/dashboard");
        send(to, "Welcome to StartupHub", html);
    }

    @Async
    @Override
    public void sendTrialExpiringEmail(String to, String name, String companyName, int daysLeft) {
        String message = "Hi " + name + ", your " + companyName + " trial expires in " + daysLeft +
                (daysLeft == 1 ? " day" : " days") + ". Upgrade now to keep access.";
        EmailBranding.Data branding = EmailBranding.Data.builder()
                .companyName(companyName)
                .build();
        String html = template.build(branding, "", message, "Upgrade Now", frontendUrl + "/upgrade");
        send(to, "Your trial is expiring soon — " + companyName, html);
    }
}