package com.StartupSAAS.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "clients")
public class Client extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(columnDefinition = "TEXT")
  private String billingAddress;

  @ManyToOne
  @JoinColumn(name = "company_id")
  @JsonIgnore
  private Company company;

  @OneToOne
  @JoinColumn(name = "user_id")
  private User user;
}
