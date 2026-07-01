package com.startuphub.scheduler;

import com.startuphub.repository.InvoiceRepository;
import com.startuphub.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class InvoiceOverdueScheduler {

    private final InvoiceRepository       invoiceRepository;
    private final NotificationRepository  notificationRepository;

    /**
     * Marks ISSUED and PARTIALLY_PAID invoices as OVERDUE when their dueDate has passed.
     * Runs daily at 01:30. Uses a single bulk UPDATE.
     */
    @Scheduled(cron = "0 30 1 * * *")
    @Transactional
    public void markOverdueInvoices() {
        int count = invoiceRepository.markOverdueInvoices(LocalDate.now());
        if (count > 0) {
            log.info("InvoiceOverdueScheduler: marked {} invoices as OVERDUE", count);
        }
    }

    /**
     * Cleans up read notifications older than 90 days.
     * Runs monthly on the 1st at 03:30.
     */
    @Scheduled(cron = "0 30 3 1 * *")
    @Transactional
    public void cleanOldNotifications() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(90);
        int count = notificationRepository.deleteReadOlderThan(cutoff);
        if (count > 0) {
            log.info("NotificationCleanup: deleted {} read notifications older than 90 days", count);
        }
    }
}
