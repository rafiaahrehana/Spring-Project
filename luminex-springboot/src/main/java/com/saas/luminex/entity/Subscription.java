package com.saas.luminex.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "subscriptions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Subscription extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(name = "features_json", columnDefinition = "TEXT")
    private String featuresJson;   // JSON array stored as text

    @Column(name = "is_recommended")
    @Builder.Default
    private boolean recommended = false;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean isActive = true;
}
