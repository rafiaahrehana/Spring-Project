package com.StartupSAAS.dto.response;

import com.StartupSAAS.enums.Gender;
import com.StartupSAAS.location.response.AddressResponse;
import com.StartupSAAS.enums.Designation;
import com.StartupSAAS.enums.Role;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class EmployeeResponse {

  private Long id;

  private String name;
  private String email;
  private String phone;
  private String image;
  private Role role;
  private Boolean active;

   private Designation designation;
  private LocalDate dob;
  private Gender gender;

  private Long companyId;
  private String companyName;

  private AddressResponse address;
}
