package com.startuphub.entity;

import com.startuphub.enums.LeadStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Filter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "leads",
    indexes = {
        @Index(name = "idx_lead_company",   columnList = "company_id"),
        @Index(name = "idx_lead_status",    columnList = "company_id, status"),
        @Index(name = "idx_lead_assigned",  columnList = "assigned_to_id")
    }
)
@Filter(name = "tenantFilter", condition = "company_id = :companyId")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lead extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String contactName;

    @Column(length = 150)
    private String companyName;

    @Column(length = 255)
    private String email;

    @Column(length = 30)
    private String phone;

    @Column(length = 100)
    private String industry;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    @Builder.Default
    private LeadStatus status = LeadStatus.NEW;

    @Column(name = "estimated_value", precision = 12, scale = 2)
    private BigDecimal estimatedValue;

    @Column(name = "converted_at")
    private LocalDateTime convertedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to_id")
    private Employee assignedTo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interested_service_id")
    private HubService interestedService;

    /**
     * Set when lead is WON and converted to a Client.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "converted_client_id")
    private Client convertedClient;
}
