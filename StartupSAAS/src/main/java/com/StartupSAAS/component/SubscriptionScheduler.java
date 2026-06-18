package com.StartupSAAS.component;

import com.StartupSAAS.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.awt.*;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class SubscriptionScheduler {

    private final CompanyRepository companyRepository;

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void deactivateExpiredCompanies() {
        companyRepository.findByActiveAndSubscriptionEndBefore(true, LocalDate.now())
                .forEach(company -> company.setActive(false));
    }
}
