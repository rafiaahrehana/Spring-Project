package com.StartupSAAS.entity.address;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Division {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name="country_id")
    private Country country;
}
