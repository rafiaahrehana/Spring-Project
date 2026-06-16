package com.StartupSAAS.entity;

import com.StartupSAAS.entity.address.Address;
import com.StartupSAAS.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import java.util.Collection;
import java.util.List;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Table(name = "users")
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
  private String name;

  private String phone;

  @Column(nullable = false, unique = true)
  private String email;

  @Column(nullable = false)
  @Size(max = 20, min = 6)
  private String password;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 40)
  private Role role;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "address_id")
  private Address address;

  private boolean isActive;

  private String image;

  @OneToOne(mappedBy = "user")
  private Company company;

  @OneToOne(mappedBy = "user")
  private Employee employee;

  @Override
  public @NonNull Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
  }

  @Override
  public @NonNull String getUsername() {
    return email;
  }

  @Override
  public boolean isAccountNonLocked() {
    return isActive;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return UserDetails.super.isCredentialsNonExpired();
  }

  @Override
  public boolean isEnabled() {
    return isActive;
  }
}
