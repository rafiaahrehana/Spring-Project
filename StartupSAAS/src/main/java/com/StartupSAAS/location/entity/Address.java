package com.StartupSAAS.location.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "address")
@Getter
@Setter
@NoArgsConstructor
public class Address {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String houseNo;
  private String road;

  private String postOffice;

  @ManyToOne
  @JoinColumn(name = "police_station_id")
  private PoliceStation policeStation;
}