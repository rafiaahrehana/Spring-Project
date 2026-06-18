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
@Table(name = "districts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class District extends BaseEntity {

    @Column(nullable = false)
    private String name;      // e.g. "Dhaka"

    private String nameBn;    // e.g. "ঢাকা"

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "division_id", nullable = false)
    private Division division;

    @OneToMany(mappedBy = "district", fetch = FetchType.LAZY)
    private List<PoliceStation> policeStations = new ArrayList<>();
}
