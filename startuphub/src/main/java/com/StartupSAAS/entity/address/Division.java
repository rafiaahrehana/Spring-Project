package com.StartupSAAS.entity.address;

import com.StartupSAAS.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "divisions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Division extends BaseEntity {

    @Column(nullable = false)
    private String name;       // e.g. "Dhaka"

    private String nameBn;     // e.g. "ঢাকা"

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id", nullable = false)
    private Country country;

    @OneToMany(mappedBy = "division", fetch = FetchType.LAZY)
    private List<District> districts = new ArrayList<>();
}
