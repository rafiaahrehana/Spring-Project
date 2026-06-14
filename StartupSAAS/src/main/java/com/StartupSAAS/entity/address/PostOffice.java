package com.StartupSAAS.entity.address;

import jakarta.persistence.*;

@Entity
public class PostOffice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String postalCode;

    @ManyToOne
    private PoliceStation policeStation;
}
