package com.StartupSAAS.entity.address;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class PoliceStation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name="ditrict_id")
    private District district;
}
