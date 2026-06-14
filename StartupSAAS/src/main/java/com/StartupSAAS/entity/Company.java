package com.StartupSAAS.entity;
import com.StartupSAAS.entity.address.Address;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "companies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Company extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(unique = true)
    private String email;
    private String phone;

    @Column(nullable = false, unique = true)
    private String subdomain;
    private String logo;
    private String website;

@OneToOne
@JoinColumn(name="owner_id")
    private User user;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id")
    private Address address;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
    private List<User> users;
}