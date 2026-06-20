package com.StartupSAAS.entity;

import com.StartupSAAS.enums.Priority;
import com.StartupSAAS.enums.RequestStatus;
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
@Table(name = "service_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ServiceRequest extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status = RequestStatus.PENDING;

    @Enumerated(EnumType.STRING)
    private Priority priority = Priority.NORMAL;

    private LocalDate deadline;

    private LocalDateTime assignedAt;

    private LocalDateTime completedAt;

    // Agreed price for this specific request
    private Double agreedPrice;

    // Current workflow stage
    private Integer currentStage = 0;

    // SLA tracking — hours allowed
    private Integer slaHours;

    // Which service was requested
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hub_service_id", nullable = false)
    private HubService hubService;

    // Which client made the request
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    // Which company handles this request
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    // Employee assigned to handle this request
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_employee_id")
    private Employee assignedEmployee;

    // Tasks under this request
    @OneToMany(mappedBy = "serviceRequest",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<Task> tasks = new ArrayList<>();
}

