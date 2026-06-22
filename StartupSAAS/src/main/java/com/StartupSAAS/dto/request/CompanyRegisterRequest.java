package com.StartupSAAS.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CompanyRegisterRequest {

    @NotBlank(message = "Company name is required")
    private String companyName;

    @Email(message = "Invalid company email format")
    private String companyEmail;
    private String companyPhone;

    @NotBlank(message = "Subdomain is required")
    private String subdomain;

    private String website;

    //for owner
    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;
    private String phone;

    private String houseNo;
    private String road;
    private String postOffice;
    private Long policeStationId;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;


}
