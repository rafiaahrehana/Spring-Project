package com.StartupSAAS.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeeResponse {
    private Long id;

    private String name;
    private String email;
    private String phone;

    private String role;
    private String designation;
    private String companyName;

    private String houseNo;
    private String road;
    private String postalCode;
    private String postOffice;
    private String policeStation;
    private String district;
    private String division;
    private String country;
}
