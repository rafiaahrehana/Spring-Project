package com.startuphub.repository;

import com.startuphub.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByIdAndCompanyId(Long id, Long companyId);

    Page<Payment> findByCompanyId(Long companyId, Pageable pageable);

    List<Payment> findByInvoiceId(Long invoiceId);

    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.invoice.id = :invoiceId AND p.deleted = false")
    Optional<BigDecimal> sumPaidAmountByInvoiceId(@Param("invoiceId") Long invoiceId);
}
