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
@Table(name = "police_stations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PoliceStation extends BaseEntity {

    @Column(nullable = false)
    private String name;      // e.g. "Mirpur"

    private String nameBn;    // e.g. "মিরপুর"

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "district_id", nullable = false)
    private District district;

    @OneToMany(mappedBy = "policeStation", fetch = FetchType.LAZY)
    private List<PostOffice> postOffices = new ArrayList<>();
}
