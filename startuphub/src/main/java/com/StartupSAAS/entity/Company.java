package com.StartupSAAS.entity;

import com.StartupSAAS.enums.CompanyStatus;
import com.StartupSAAS.enums.SubscriptionPlan;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "companies")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Company extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String subdomain;  // e.g. "acme" → acme.startuphub.com

    // Company Owner — the user who created this workspace
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubscriptionPlan plan = SubscriptionPlan.STARTER;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CompanyStatus status = CompanyStatus.TRIAL;

    private String logoUrl;

    private String primaryColor = "#1B2A4A";    // navy

    private String secondaryColor = "#C9A84C";  // gold

    // Plain text address for company — doc says TEXT field
    @Column(columnDefinition = "TEXT")
    private String address;

    private String website;

    private LocalDateTime trialEndsAt;

    private LocalDate billingCycleStart;

    // Users in this company
    @OneToMany(mappedBy = "company", fetch = FetchType.LAZY)
    private List<User> users = new ArrayList<>();

    // Employees in this company
    @OneToMany(mappedBy = "company", fetch = FetchType.LAZY)
    private List<Employee> employees = new ArrayList<>();

    // Clients in this company
    @OneToMany(mappedBy = "company", fetch = FetchType.LAZY)
    private List<Client> clients = new ArrayList<>();
}
