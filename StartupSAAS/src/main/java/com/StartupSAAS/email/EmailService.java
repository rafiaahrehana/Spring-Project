package com.StartupSAAS.email;

public interface EmailService {


    void send(
            String to,
            String subject,
            String html
    );


    void sendVerificationEmail(
            String to,
            String name,
            String token,
            EmailBranding branding
    );


    void sendWelcomeEmail(
            String to,
            String name,
            EmailBranding branding
    );

}