package com.StartupSAAS.dto.request;

import com.StartupSAAS.enums.Designation;
import com.StartupSAAS.enums.Gender;
import com.StartupSAAS.enums.Role;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
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

  @NotNull(message = "Date of Birth is required")
  @Past(message = "Date of birth must be in the past")
  private LocalDate dob;
  private Gender gender;
  private String houseNo;
  private String road;

  private String postOffice;
  private Long policeStationId;

}
