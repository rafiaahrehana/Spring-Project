package com.StartupSAAS.entity;

import com.StartupSAAS.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class User extends BaseEntity {

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;  // BCrypt hashed

    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    // NULL for SUPER_ADMIN — platform level user
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    private String profilePictureUrl;

    // Notification preferences
    private String languagePref = "EN";  // EN or BN

    private Boolean emailEnabled = true;

    private Boolean smsEnabled = false;

    private Boolean emailInvalid = false; // flagged after 3 bounced emails

    private Boolean isActive = true;
}
