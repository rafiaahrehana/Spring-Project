package com.startuphub.repository;

import com.startuphub.entity.Client;
import com.startuphub.enums.ClientStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {

    Optional<Client> findByUserId(Long userId);

    Optional<Client> findByIdAndCompanyId(Long id, Long companyId);

    boolean existsByUserIdAndCompanyId(Long userId, Long companyId);

    Page<Client> findByCompanyId(Long companyId, Pageable pageable);

    Page<Client> findByCompanyIdAndStatus(Long companyId, ClientStatus status, Pageable pageable);

    long countByCompanyId(Long companyId);

    /**
     * Resolves the company ID for a user who is a client.
     * Used by AuthServiceImpl.resolveCompanyId() in Phase 3+.
     */
    @Query("SELECT c.company.id FROM Client c WHERE c.user.id = :userId AND c.deleted = false")
    Optional<Long> findCompanyIdByUserId(Long userId);
}
