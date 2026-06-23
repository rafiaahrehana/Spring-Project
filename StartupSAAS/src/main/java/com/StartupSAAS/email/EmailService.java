package com.StartupSAAS.email;

public interface EmailService {

    void send(String to, String subject, String html);

    void sendVerificationEmail(String to, String name, String token, EmailBranding.Data branding);

    void sendWelcomeEmail(String to, String name, EmailBranding.Data branding);

    void sendTrialExpiringEmail(String to, String name, String companyName, int daysLeft);
}