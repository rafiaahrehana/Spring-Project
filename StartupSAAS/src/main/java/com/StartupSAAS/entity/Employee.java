package com.StartupSAAS.entity;

import com.StartupSAAS.enums.Designation;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "employees")
public class Employee extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Enumerated(EnumType.STRING)
  private Designation designation;

  @OneToOne
  @JoinColumn(name = "employee_id")
  private User user;

  @ManyToOne
  @JoinColumn(name = "company_id")
  @JsonIgnore
  private Company company;


}
