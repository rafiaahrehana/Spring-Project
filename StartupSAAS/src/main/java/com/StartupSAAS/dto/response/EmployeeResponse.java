package com.StartupSAAS.dto.response;

import com.StartupSAAS.location.response.AddressResponse;
import com.StartupSAAS.enums.Designation;
import com.StartupSAAS.enums.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeeResponse {
  private Long id;
  private String name;
  private String email;
  private String phone;
  private Role role;
  private Designation designation;
  private Long companyId;
  private String companyName;
  private AddressResponse address;
  private String image;
}
