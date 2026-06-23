package com.StartupSAAS.email;


import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.*;
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

        try {MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message,true);

            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html,true);
            mailSender.send(message);
            log.info("Email sent to {}", to);

        } catch (Exception e){
            log.error("Email failed {}", to, e);
        }
    }

    @Override
    public void sendVerificationEmail(String to, String name, String token, EmailBranding branding) {
        String link = frontendUrl + "/verify-email?token=" + token;

        String html = template.build(branding, "Verify Account", "Welcome "+name+ "," +
                " please verify your email.", "Verify Email", link);
        send(to, "Verify your StartupHub account", html);
    }

    @Override
    public void sendWelcomeEmail(String to, String name, EmailBranding branding){
        String html = template.build(branding, "Welcome "+name, "Your company account is ready.",
                        "Open Dashboard", frontendUrl+"/dashboard");
        send(to, "Welcome to StartupHub", html);
    }

}