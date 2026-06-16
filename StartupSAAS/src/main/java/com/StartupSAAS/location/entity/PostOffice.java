package com.StartupSAAS.location.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "post_offices")
@Getter
@Setter
@NoArgsConstructor
public class PostOffice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String postalCode;

    @ManyToOne
    @JoinColumn(name = "policeStation_id")
    private PoliceStation policeStation;
}