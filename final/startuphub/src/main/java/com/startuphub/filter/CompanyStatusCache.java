package com.startuphub.filter;

import com.startuphub.enums.CompanyStatus;
import com.startuphub.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

/**
 * Caches company blocked status to eliminate the per-request DB query in
 * CompanyActiveFilter.
 *
 * Cache TTL is configured in application.yml:
 *   spring.cache.caffeine.spec=maximumSize=1000,expireAfterWrite=60s
 *
 * pom.xml requires:
 *   <dependency>
 *     <groupId>com.github.ben-manes.caffeine</groupId>
 *     <artifactId>caffeine</artifactId>
 *   </dependency>
 *   <dependency>
 *     <groupId>org.springframework.boot</groupId>
 *     <artifactId>spring-boot-starter-cache</artifactId>
 *   </dependency>
 *
 * And StartupHubApplication must be annotated with @EnableCaching.
 */
@Component
@RequiredArgsConstructor
public class CompanyStatusCache {

    private final CompanyRepository companyRepository;

    @Cacheable(value = "companyStatus", key = "#companyId")
    public boolean isBlockedStatus(Long companyId) {
        return companyRepository.findById(companyId)
            .map(c -> c.getStatus() == CompanyStatus.SUSPENDED
                   || c.getStatus() == CompanyStatus.DEACTIVATED)
            .orElse(false);
    }

    /**
     * Call after any company status change (CompanyServiceImpl.updateStatus()).
     */
    @CacheEvict(value = "companyStatus", key = "#companyId")
    public void evict(Long companyId) {}
}
