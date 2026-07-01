package com.startuphub.entity;

import com.startuphub.enums.ClientStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Filter;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "clients",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_client_user_company",
            columnNames = {"user_id", "company_id"})
    },
    indexes = {
        @Index(name = "idx_client_company", columnList = "company_id"),
        @Index(name = "idx_client_user",    columnList = "user_id")
    }
)
@Filter(name = "tenantFilter", condition = "company_id = :companyId")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Client extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Portal identity — @OneToOne User.
     * No cascade: User lifecycle managed in service layer.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_manager_id")
    private Employee accountManager;

    // Client's own company info
    @Column(length = 150)
    private String clientCompanyName;

    @Column(length = 100)
    private String industry;

    @Column(length = 255)
    private String website;

    // PII — encrypt in production
    @Column(length = 50)
    private String taxId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    @Builder.Default
    private ClientStatus status = ClientStatus.ACTIVE;

    @Column(nullable = false)
    private LocalDateTime onboardedAt = LocalDateTime.now();

    @Column(nullable = false)
    @Builder.Default
    private boolean portalAccessEnabled = true;
}
