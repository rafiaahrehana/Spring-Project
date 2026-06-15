package com.StartupSAAS.entity.address;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class District {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name="division_id")
    private Division division;
}
