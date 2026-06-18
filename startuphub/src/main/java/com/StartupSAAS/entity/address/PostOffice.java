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
@Table(name = "post_offices")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PostOffice extends BaseEntity {

    @Column(nullable = false)
    private String name;        // e.g. "Mirpur-1"

    @Column(nullable = false)
    private String postalCode;  // e.g. "1216"

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "police_station_id", nullable = false)
    private PoliceStation policeStation;

    @OneToMany(mappedBy = "postOffice", fetch = FetchType.LAZY)
    private List<Address> addresses = new ArrayList<>();
}
