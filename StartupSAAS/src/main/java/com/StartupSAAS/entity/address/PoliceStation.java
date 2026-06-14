package com.StartupSAAS.entity.address;

import jakarta.persistence.*;

@Entity
public class PoliceStation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne
    private District district;
}
