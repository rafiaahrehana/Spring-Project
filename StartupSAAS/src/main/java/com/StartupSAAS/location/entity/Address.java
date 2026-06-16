package com.StartupSAAS.location.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "locations")
@Getter
@Setter
@NoArgsConstructor
public class Address {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String houseNo;
  private String road;

  @ManyToOne
  @JoinColumn(name = "postoffice_id")
  private PostOffice postOffice;
}