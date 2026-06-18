package com.StartupSAAS.repository;

import com.StartupSAAS.entity.Payment;
import com.StartupSAAS.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Query("""
        SELECT p FROM Payment p
        LEFT JOIN FETCH p.invoice
        LEFT JOIN FETCH p.client cl
        LEFT JOIN FETCH cl.user
        LEFT JOIN FETCH p.company
        WHERE p.id = :id
    """)
    Optional<Payment> findByIdWithDetails(@Param("id") Long id);

    List<Payment> findByCompanyId(Long companyId);

    List<Payment> findByClientId(Long clientId);

    List<Payment> findByInvoiceId(Long invoiceId);

    List<Payment> findByCompanyIdAndStatus(Long companyId, PaymentStatus status);
}
