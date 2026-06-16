package com.StartupSAAS.dto.request;

import com.StartupSAAS.enums.Designation;
import com.StartupSAAS.enums.Role;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class EmployeeRequest {

  private String name;
  private String email;
  private String password;
  private String phone;
  private Role role;
  private Designation designation;
  private LocalDate hireDate;

  private String houseNo;
  private String road;
  private String postalCode;
  private Long postOfficeId;
  private Long policeStationId;
  private Long districtId;
  private Long divisionID;
  private Long countryId;
}
