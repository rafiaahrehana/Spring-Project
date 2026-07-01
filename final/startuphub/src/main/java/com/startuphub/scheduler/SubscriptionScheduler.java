package com.startuphub.scheduler;

import com.startuphub.entity.Company;
import com.startuphub.enums.CompanyStatus;
import com.startuphub.repository.CompanyRepository;
import com.startuphub.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class SubscriptionScheduler {

    private final CompanyRepository companyRepository;
    private final EmailService      emailService;

    @Value("${app.trial-reminder-days:3}")
    private int trialReminderDays;

    @Scheduled(cron = "0 5 0 * * *")
    @Transactional
    public void suspendExpiredCompanies() {
        List<Company> expired = companyRepository.findExpiredSubscriptions(
            LocalDate.now(),
            List.of(CompanyStatus.TRIAL, CompanyStatus.ACTIVE)
        );
        if (expired.isEmpty()) return;

        for (Company company : expired) {
            company.setStatus(CompanyStatus.SUSPENDED);
            try {
                if (company.getOwner() != null) {
                    emailService.sendSubscriptionSuspendedEmail(
                        company.getOwner().getEmail(),
                        company.getOwner().getFirstName(),
                        company.getCompanyName());
                }
            } catch (Exception e) {
                log.error("Suspension email failed for company {}: {}",
                    company.getId(), e.getMessage());
            }
        }
        log.info("Suspended {} expired companies", expired.size());
    }

    @Scheduled(cron = "0 0 9 * * *")
    @Transactional
    public void sendTrialExpiryReminders() {
        LocalDate today  = LocalDate.now();
        LocalDate cutoff = today.plusDays(trialReminderDays);

        List<Company> approaching = companyRepository.findTrialExpiringBetween(
            today, cutoff, CompanyStatus.TRIAL
        );
        if (approaching.isEmpty()) return;

        for (Company company : approaching) {
            try {
                if (company.getOwner() == null) continue;
                long daysLeft = ChronoUnit.DAYS.between(today, company.getSubscriptionEnd());
                emailService.sendTrialExpiringEmail(
                    company.getOwner().getEmail(),
                    company.getOwner().getFirstName(),
                    company.getCompanyName(),
                    (int) daysLeft);
                company.setTrialReminderSentAt(LocalDateTime.now());
            } catch (Exception e) {
                log.error("Trial reminder failed for company {}: {}",
                    company.getId(), e.getMessage());
            }
        }
        log.info("Sent {} trial expiry reminders", approaching.size());
    }
}
