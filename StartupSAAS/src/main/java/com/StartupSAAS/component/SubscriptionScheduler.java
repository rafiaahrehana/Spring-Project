package com.StartupSAAS.component;

import com.StartupSAAS.email.EmailService;
import com.StartupSAAS.entity.Company;
import com.StartupSAAS.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@Slf4j
public class SubscriptionScheduler {

    private static final int TRIAL_REMINDER_DAYS_BEFORE = 3;

    private final CompanyRepository companyRepository;
    private final EmailService emailService;

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void deactivateExpiredCompanies() {
        companyRepository.findByActiveAndSubscriptionEndBefore(true, LocalDate.now())
                .forEach(company -> company.setActive(false));
    }

    @Scheduled(cron = "0 0 9 * * *")
    @Transactional
    public void notifyExpiringTrials() {
        LocalDate cutoff = LocalDate.now().plusDays(TRIAL_REMINDER_DAYS_BEFORE);
        companyRepository.findByActiveAndTrialReminderSentFalseAndSubscriptionEndLessThanEqual(true, cutoff)
                .forEach(this::sendTrialReminder);
    }

    private void sendTrialReminder(Company company) {
        if (company.getUser() == null || company.getSubscriptionEnd() == null)
            return;

        long daysLeft = LocalDate.now().until(company.getSubscriptionEnd()).getDays();

        emailService.sendTrialExpiringEmail(
                company.getUser().getEmail(),
                company.getUser().getFirstName(),
                company.getCompanyName(),
                (int) Math.max(daysLeft, 0));

        company.setTrialReminderSent(true);
        log.info("Trial reminder sent for company {}", company.getId());
    }
}