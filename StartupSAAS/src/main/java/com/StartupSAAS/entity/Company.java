package com.StartupSAAS.entity;

import com.StartupSAAS.enums.SubscriptionPlan;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "companies")
@Getter
@Setter
@NoArgsConstructor
public class Company extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String companyName;

  @Column(unique = true)
  private String companyEmail;

  @Column(unique = true)
  private String companyPhone;

  private String logo;
  private String website;

  @Column(nullable = false, unique = true)
  private String subdomain;

  @OneToOne
  @JoinColumn(name = "user_id")
  private User user;

  @Enumerated(EnumType.STRING)
  private SubscriptionPlan subscriptionPlan;
}