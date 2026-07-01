package com.startuphub.repository;

import com.startuphub.entity.HubService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HubServiceRepository extends JpaRepository<HubService, Long> {

    Page<HubService> findByCompanyId(Long companyId, Pageable pageable);

    Page<HubService> findByCompanyIdAndCategoryId(Long companyId, Long categoryId, Pageable pageable);

    List<HubService> findByCompanyIdAndActiveTrue(Long companyId);

    Optional<HubService> findByIdAndCompanyId(Long id, Long companyId);

    boolean existsByCompanyIdAndNameAndIdNot(Long companyId, String name, Long excludeId);
}
