package com.StartupSAAS.entity;

import com.StartupSAAS.enums.SubscriptionPlan;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "companies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Company extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;

  @Column(unique = true)
  private String email;

  private String phone;

  @Column(nullable = false, unique = true)
  private String subdomain;

  private String logo;
  private String website;

  @OneToOne
  @JoinColumn(name = "owner_id")
  private User user;

  @Enumerated(EnumType.STRING)
  private SubscriptionPlan subscriptionPlan;
}
