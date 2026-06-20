package com.StartupSAAS.entity;

import com.StartupSAAS.enums.PriceType;
import com.StartupSAAS.enums.Priority;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "hub_services")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HubService extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String iconUrl;

    private Double price;

    @Enumerated(EnumType.STRING)
    private PriceType priceType = PriceType.FIXED;

    // Estimated days to complete
    private Integer estimatedDays;

    @Enumerated(EnumType.STRING)
    private Priority defaultPriority = Priority.NORMAL;

    private Boolean active = true;

    // Which company offers this service
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    // Workflow template attached to this service
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_template_id")
    private WorkflowTemplate workflowTemplate;

    @OneToMany(mappedBy = "hubService", fetch = FetchType.LAZY)
    private List<ServiceRequest> serviceRequests = new ArrayList<>();
}