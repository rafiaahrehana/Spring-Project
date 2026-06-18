package com.StartupSAAS.repository;

import com.StartupSAAS.entity.Invoice;
import com.StartupSAAS.enums.InvoiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    @Query("""
        SELECT i FROM Invoice i
        LEFT JOIN FETCH i.serviceRequest
        LEFT JOIN FETCH i.client cl
        LEFT JOIN FETCH cl.user
        LEFT JOIN FETCH i.company
        WHERE i.id = :id
    """)
    Optional<Invoice> findByIdWithDetails(@Param("id") Long id);

    @Query("""
        SELECT i FROM Invoice i
        LEFT JOIN FETCH i.serviceRequest
        LEFT JOIN FETCH i.client cl
        LEFT JOIN FETCH cl.user
        LEFT JOIN FETCH i.company
        WHERE i.company.id = :companyId
    """)
    List<Invoice> findByCompanyId(@Param("companyId") Long companyId);

    @Query("""
        SELECT i FROM Invoice i
        LEFT JOIN FETCH i.serviceRequest
        LEFT JOIN FETCH i.client cl
        LEFT JOIN FETCH cl.user
        LEFT JOIN FETCH i.company
        WHERE i.client.id = :clientId
    """)
    List<Invoice> findByClientId(@Param("clientId") Long clientId);

    List<Invoice> findByCompanyIdAndStatus(Long companyId, InvoiceStatus status);

    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);
}
