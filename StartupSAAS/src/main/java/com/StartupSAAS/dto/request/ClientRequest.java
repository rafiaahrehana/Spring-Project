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

  @NotBlank(message = "Password is required")
  private String password;

  private String phone;

  private String billingAddress;

  private String houseNo;

  private String road;

  private String country;
  private String division;
  private String district;
  private String policeStation;
  private String postOffice;
}
