package com.StartupSAAS.repository;

import com.StartupSAAS.entity.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    boolean existsBySubdomain(String subdomain);
    Page<Company> findByIsActive(boolean isActive, Pageable pageable);
    Page<Company> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
