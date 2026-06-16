package com.StartupSAAS.dto.request;

import com.StartupSAAS.enums.Designation;
import com.StartupSAAS.enums.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeeRequest {

  private String name;
  private String email;
  private String password;
  private String phone;
  private Role role;
  private Designation designation;

  private String houseNo;
  private String road;
  private Long postalCode;
  private Long postOfficeId;
  private Long policeStationId;
  private Long districtId;
  private Long divisionID;
  private Long countryId;
}
