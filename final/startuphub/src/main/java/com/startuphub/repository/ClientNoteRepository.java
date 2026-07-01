package com.startuphub.repository;

import com.startuphub.entity.ClientNote;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientNoteRepository extends JpaRepository<ClientNote, Long> {

    Optional<ClientNote> findByIdAndCompanyId(Long id, Long companyId);

    Page<ClientNote> findByClientIdOrderByCreatedAtDesc(Long clientId, Pageable pageable);
}
