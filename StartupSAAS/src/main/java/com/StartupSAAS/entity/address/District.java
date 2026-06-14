package com.StartupSAAS.entity.address;

import jakarta.persistence.*;

@Entity
public class District {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne
    private Division division;
}
