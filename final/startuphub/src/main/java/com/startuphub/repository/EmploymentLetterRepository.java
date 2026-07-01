package com.startuphub.repository;

import com.startuphub.entity.EmploymentLetter;
import com.startuphub.enums.LetterType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmploymentLetterRepository extends JpaRepository<EmploymentLetter, Long> {

    Optional<EmploymentLetter> findByIdAndCompanyId(Long id, Long companyId);

    Page<EmploymentLetter> findByCompanyIdAndEmployeeId(
        Long companyId, Long employeeId, Pageable pageable);

    Page<EmploymentLetter> findByCompanyId(Long companyId, Pageable pageable);

    boolean existsByCompanyIdAndReferenceNumber(Long companyId, String referenceNumber);

    long countByCompanyIdAndLetterType(Long companyId, LetterType letterType);
}
