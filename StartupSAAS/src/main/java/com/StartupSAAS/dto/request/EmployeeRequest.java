package com.StartupSAAS.dto.request;
import com.StartupSAAS.enums.Designation;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeeRequest {

    private String name;
    private String email;
    private String password;
    private Designation designation;
    private String phone;

    private String houseNo;
    private String road;
    private String postalCode;
    private String postOffice;
    private String policeStation;
    private String district;
    private String division;
    private String country;
}
