package com.StartupSAAS.entity.address;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
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
