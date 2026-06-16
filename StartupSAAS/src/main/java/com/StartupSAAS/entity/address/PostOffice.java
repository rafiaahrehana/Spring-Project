package com.StartupSAAS.entity.address;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class PostOffice {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;
  private String postalCode;

  @ManyToOne
  @JoinColumn(name = "police_station_id")
  private PoliceStation policeStation;
}
