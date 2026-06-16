package com.StartupSAAS.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ClientRequest {
  @NotBlank(message = "Name is required")
  private String name;

  @Email(message = "Invalid email format")
  @NotBlank(message = "Email is required")
  private String email;
  private String phone;

  @NotBlank(message = "Password is required")
  private String password;

  private String billingAddress;
  private String houseNo;
  private String road;
  private Long postalCode;
  private Long postOfficeId;
  private Long policeStationId;
  private Long districtId;
  private Long divisionID;
  private Long countryId;

}
