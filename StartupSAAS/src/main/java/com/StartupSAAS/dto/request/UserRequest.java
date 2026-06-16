package com.StartupSAAS.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserRequest {

  @NotBlank(message = "First name is required")
  private String firstName;

  @NotBlank(message = "Last name is required")
  private String lastName;

  @NotBlank(message = "Email is required")
  @Email(message = "Invalid email format")
  private String email;

  private String phone;

  // address
  private String houseNo;
  private String road;
  private Long postOfficeId;
  private Long policeStationId;
  private Long districtId;
  private Long divisionId;
  private Long countryId;
}