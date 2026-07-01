package com.startuphub.scheduler;

import com.startuphub.enums.JobPostingStatus;
import com.startuphub.repository.JobPostingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@Slf4j
public class JobDeadlineScheduler {

    private final JobPostingRepository jobPostingRepository;

    /**
     * Closes all OPEN job postings whose deadline has passed.
     * Runs daily at 01:00. Uses bulk UPDATE for efficiency.
     */
    @Scheduled(cron = "0 0 1 * * *")
    @Transactional
    public void closeExpiredJobPostings() {
        int count = jobPostingRepository.closeExpiredPostings(
            LocalDate.now(),
            JobPostingStatus.OPEN,
            JobPostingStatus.CLOSED
        );
        if (count > 0) {
            log.info("JobDeadlineScheduler: closed {} expired job postings", count);
        }
    }
}
