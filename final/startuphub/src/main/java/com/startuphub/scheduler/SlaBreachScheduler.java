package com.startuphub.scheduler;

import com.startuphub.enums.ServiceRequestStatus;
import com.startuphub.repository.ServiceRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class SlaBreachScheduler {

    private final ServiceRequestRepository serviceRequestRepository;

    private static final List<ServiceRequestStatus> CLOSED_STATUSES = List.of(
        ServiceRequestStatus.COMPLETED,
        ServiceRequestStatus.CANCELLED,
        ServiceRequestStatus.REJECTED
    );

    /**
     * Marks slaBreach = true for all open requests past their slaDeadline.
     * Uses a single bulk UPDATE — does not load any entities into memory.
     * Runs every 30 minutes.
     */
    @Scheduled(cron = "0 */30 * * * *")
    @Transactional
    public void markSlaBreaches() {
        int count = serviceRequestRepository.bulkMarkSlaBreaches(
            LocalDateTime.now(), CLOSED_STATUSES);
        if (count > 0) {
            log.info("SlaBreachScheduler: marked {} service requests as SLA breached", count);
        }
    }
}
