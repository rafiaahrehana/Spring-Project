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
@Table(name = "countries")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Country extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String name;

    private String code; // e.g. "BD"

    @OneToMany(mappedBy = "country", fetch = FetchType.LAZY)
    private List<Division> divisions = new ArrayList<>();
}
