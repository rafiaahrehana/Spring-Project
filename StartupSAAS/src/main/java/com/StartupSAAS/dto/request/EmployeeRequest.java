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
    private Designation designation;
    private String phone;
    private Role role;

    private String houseNo;
    private String road;
    private String postalCode;
    private String postOffice;
    private String policeStation;
    private String district;
    private String division;
    private String country;
}
