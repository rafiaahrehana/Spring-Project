package com.StartupSAAS.email;

public interface EmailService {

    void sendSimpleMail(String to, String subject, String body);

    void sendVerificationEmail(String to, String name, String token);

    void sendPasswordResetEmail(String to, String name, String token);

    void sendWelcomeEmail(String to, String name, String companyName);

    void sendTrialExpiringEmail(String to, String name, String companyName, int daysLeft);
}
