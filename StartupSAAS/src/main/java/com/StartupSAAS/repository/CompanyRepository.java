package com.StartupSAAS.repository;

import com.StartupSAAS.entity.Company;
import com.StartupSAAS.enums.SubscriptionPlan;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    boolean existsBySubdomain(String subdomain);
    Optional<Company> findByUserId(Long userId);
    Page<Company> findByCompanyNameContainingIgnoreCase(String name, Pageable pageable);
    Page<Company> findBySubscriptionPlan(SubscriptionPlan subscriptionPlan, Pageable pageable);
    List<Company> findByActiveAndSubscriptionEndBefore(boolean active, LocalDate date);
}