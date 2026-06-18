package com.StartupSAAS.entity;

import com.StartupSAAS.entity.address.Address;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "employees")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Employee extends BaseEntity {

    private String designation;

    private String department;

    private String image;

    private Boolean active = true;

    // Auth account — name, email, password, role=EMPLOYEE lives here
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Which company this employee belongs to
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    // Full validated address:
    // street → PostOffice (postalCode) → PoliceStation → District → Division → Country
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id")
    private Address address;

    // NID for employee verification
    private String nidNumber;

    // Emergency contact
    private String emergencyContact;
}
