package com.startuphub.entity;

import com.startuphub.enums.ServiceRequestPriority;
import com.startuphub.enums.ServiceRequestStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Filter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
    name = "service_requests",
    indexes = {
        @Index(name = "idx_sr_company",   columnList = "company_id"),
        @Index(name = "idx_sr_client",    columnList = "client_id"),
        @Index(name = "idx_sr_service",   columnList = "hub_service_id"),
        @Index(name = "idx_sr_status",    columnList = "company_id, status"),
        @Index(name = "idx_sr_assigned",  columnList = "assigned_employee_id")
    }
)
@Filter(name = "tenantFilter", condition = "company_id = :companyId")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceRequest extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private ServiceRequestStatus status = ServiceRequestStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    @Builder.Default
    private ServiceRequestPriority priority = ServiceRequestPriority.NORMAL;

    @Column(name = "agreed_price", precision = 12, scale = 2)
    private BigDecimal agreedPrice;

    @Column(name = "sla_deadline")
    private LocalDateTime slaDeadline;

    @Column(name = "sla_breach", nullable = false)
    @Builder.Default
    private boolean slaBreach = false;

    @Column(name = "assigned_at")
    private LocalDateTime assignedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "resubmit_count", nullable = false)
    @Builder.Default
    private int resubmitCount = 0;

    @Column(name = "permanently_closed", nullable = false)
    @Builder.Default
    private boolean permanentlyClosed = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hub_service_id", nullable = false)
    private HubService hubService;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_employee_id")
    private Employee assignedEmployee;

    /**
     * Tasks: CascadeType.PERSIST + MERGE only.
     * orphanRemoval is intentionally FALSE — tasks are operational records
     * and must never be physically deleted by cascade.
     * Soft-delete tasks via task.softDelete() in service layer.
     */
    @OneToMany(
        mappedBy = "serviceRequest",
        cascade = {CascadeType.PERSIST, CascadeType.MERGE},
        fetch = FetchType.LAZY
    )
    @BatchSize(size = 20)
    @Builder.Default
    private List<Task> tasks = new ArrayList<>();

    @OneToMany(
        mappedBy = "serviceRequest",
        cascade = {CascadeType.PERSIST, CascadeType.MERGE},
        fetch = FetchType.LAZY
    )
    @OrderBy("createdAt DESC")
    @BatchSize(size = 10)
    @Builder.Default
    private List<RequestComment> comments = new ArrayList<>();

    @OneToMany(
        mappedBy = "serviceRequest",
        cascade = {CascadeType.PERSIST, CascadeType.MERGE},
        fetch = FetchType.LAZY
    )
    @OrderBy("changedAt ASC")
    @BatchSize(size = 10)
    @Builder.Default
    private List<RequestStatusHistory> statusHistory = new ArrayList<>();
}
