package com.startuphub.repository;

import com.startuphub.entity.Invoice;
import com.startuphub.enums.InvoiceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    Optional<Invoice> findByIdAndCompanyId(Long id, Long companyId);

    Page<Invoice> findByCompanyId(Long companyId, Pageable pageable);

    Page<Invoice> findByCompanyIdAndStatus(Long companyId, InvoiceStatus status, Pageable pageable);

    Page<Invoice> findByCompanyIdAndClientId(Long companyId, Long clientId, Pageable pageable);

    Optional<Invoice> findByCompanyIdAndServiceRequestId(Long companyId, Long serviceRequestId);

    boolean existsByCompanyIdAndInvoiceNumber(Long companyId, String invoiceNumber);

    @Query("SELECT MAX(i.invoiceNumber) FROM Invoice i WHERE i.company.id = :companyId AND i.invoiceNumber LIKE :prefix%")
    Optional<String> findMaxInvoiceNumberWithPrefix(
        @Param("companyId") Long companyId,
        @Param("prefix") String prefix);

    @Transactional
    @Modifying
    @Query("""
        UPDATE Invoice i SET i.status = 'OVERDUE'
        WHERE i.dueDate < :today
          AND i.status IN ('ISSUED', 'PARTIALLY_PAID')
          AND i.deleted = false
        """)
    int markOverdueInvoices(@Param("today") LocalDate today);

    @Query("SELECT SUM(i.totalAmount - i.paidAmount) FROM Invoice i WHERE i.company.id = :companyId AND i.status IN ('ISSUED','PARTIALLY_PAID','OVERDUE') AND i.deleted = false")
    Optional<java.math.BigDecimal> sumOutstandingByCompanyId(@Param("companyId") Long companyId);
}
