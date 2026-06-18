package com.StartupSAAS.entity;

import com.StartupSAAS.enums.InvoiceStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "invoices")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Invoice extends BaseEntity {

    // Auto-generated e.g. "INV-2024-0001"
    @Column(unique = true, nullable = false)
    private String invoiceNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvoiceStatus status = InvoiceStatus.DRAFT;

    private Double subtotal;

    private Double discountAmount = 0.0;

    private Double taxAmount = 0.0;

    private Double totalAmount;

    private Double paidAmount = 0.0;

    private LocalDate issueDate;

    private LocalDate dueDate;

    @Column(columnDefinition = "TEXT")
    private String notes;

    // Which service request this invoice is for
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_request_id")
    private ServiceRequest serviceRequest;

    // Which client this invoice is billed to
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    // Which company issued this invoice
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    // Payments made against this invoice
    @OneToMany(mappedBy = "invoice", fetch = FetchType.LAZY)
    private List<Payment> payments = new ArrayList<>();
}
