package com.startuphub.repository;

import com.startuphub.entity.Expense;
import com.startuphub.enums.ExpenseStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    Optional<Expense> findByIdAndCompanyId(Long id, Long companyId);

    Page<Expense> findByCompanyId(Long companyId, Pageable pageable);

    Page<Expense> findByCompanyIdAndStatus(Long companyId, ExpenseStatus status, Pageable pageable);

    Page<Expense> findByCompanyIdAndSubmittedById(Long companyId, Long employeeId, Pageable pageable);
}
