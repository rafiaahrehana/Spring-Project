package com.StartupSAAS.entity.address;

import com.StartupSAAS.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "addresses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Address extends BaseEntity {

    // Street / house / road / area details
    @Column(nullable = false)
    private String street;

    // PostOffice → knows postalCode
    //           → knows PoliceStation
    //                  → knows District
    //                         → knows Division
    //                                → knows Country
    // ✅ Full chain validated through one FK
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_office_id", nullable = false)
    private PostOffice postOffice;
}
