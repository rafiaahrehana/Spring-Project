package com.startuphub.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Platform-wide service taxonomy.
 *
 * The six domains are seeded by SUPER_ADMIN at platform launch:
 *   1. Legal & Compliance
 *   2. Finance & Accounting
 *   3. HR & Recruitment
 *   4. IT & Technology
 *   5. Office Space
 *   6. Office Stationery & Supplies
 *
 * These are GLOBAL — no company_id. Every tenant's HubService
 * references one of these categories. Companies cannot create
 * their own categories; they define services within these categories.
 *
 * sortOrder controls display order in the Angular catalog UI.
 */
@Entity
@Table(
    name = "service_categories",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_service_category_name", columnNames = "name")
    },
    indexes = {
        @Index(name = "idx_service_category_name", columnList = "name"),
        @Index(name = "idx_service_category_sort", columnList = "sort_order")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceCategory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    // Bangla name for i18n support
    @Column(length = 100)
    private String nameBn;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String descriptionBn;

    private String iconUrl;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder = 0;

    @Column(nullable = false)
    private boolean active = true;
}
