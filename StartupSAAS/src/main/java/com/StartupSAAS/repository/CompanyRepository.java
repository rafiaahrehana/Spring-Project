package com.StartupSAAS.repository;

import com.StartupSAAS.entity.Company;
import com.StartupSAAS.enums.SubscriptionPlan;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    boolean existsBySubdomain(String subdomain);
    Optional<Company> findByUserId(Long userId);
    Page<Company> findByCompanyNameContainingIgnoreCase(String name, Pageable pageable);
    Page<Company> findBySubscriptionPlan(SubscriptionPlan subscriptionPlan, Pageable pageable);
}