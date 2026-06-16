package com.StartupSAAS.dto.response;

import com.StartupSAAS.enums.Role;
import com.StartupSAAS.location.response.AddressResponse;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponse {
  private Long id;
  private String firstName;
  private String lastName;
  private String email;
  private String phone;
  private Role role;
  private boolean active;
  private String image;
  private AddressResponse address;
  private LocalDateTime createdAt;
}