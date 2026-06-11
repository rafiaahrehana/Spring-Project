package com.StartupSAAS.entity;

import com.StartupSAAS.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    @Size(max = 20, min = 4)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private Role role;

    @Column(name = "company_name")
    private String companyName;

    private String address;
    private String phone;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;
}
