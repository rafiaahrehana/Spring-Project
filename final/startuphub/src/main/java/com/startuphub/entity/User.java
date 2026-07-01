package com.startuphub.entity;

import com.startuphub.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Authentication identity for every actor in the system.
 *
 * A User is created for:
 *   - Company owners (at registration)
 *   - Employees (via TeamInvite in Phase 3)
 *   - Clients (via ClientController in Phase 3)
 *
 * Tenant association is resolved at login via Company/Employee/Client
 * records, then embedded in the JWT as companyId.
 *
 * Security notes:
 *   - password must be BCrypt-encoded before persisting
 *   - twoFactorSecret will be encrypted at rest via @Converter (Phase 1 hardening)
 *   - Users are NEVER hard-deleted — soft delete preserves audit trail
 *
 * Address fields are flattened here (no Address entity).
 */
@Entity
@Table(
    name = "users",
    indexes = {
        @Index(name = "idx_users_email", columnList = "email")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    // BCrypt output is always 60 chars
    @Column(nullable = false, length = 60)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    @Column(nullable = false)
    private boolean active = false;

    /**
     * emailVerified tracks whether this user's email address
     * has been confirmed via the verification link.
     * Moved here from Company — email belongs to User, not Company.
     */
    @Column(nullable = false)
    private boolean emailVerified = false;

    private String phone;
    private String image;

    // 2FA — secret stored here, encryption via @Converter added in hardening sprint
    private boolean twoFactorEnabled = false;
    private String twoFactorSecret;

    // Language preference for i18n (EN or BN)
    @Column(length = 5)
    private String languagePreference = "EN";

    // Flattened address — no Address entity
    private String addressLine;
    private String city;
    private String postalCode;
    private String country;

    // ── UserDetails implementation ─────────────────────────────

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }

    // Convenience method
    public String getFullName() {
        return firstName + " " + lastName;
    }
}
