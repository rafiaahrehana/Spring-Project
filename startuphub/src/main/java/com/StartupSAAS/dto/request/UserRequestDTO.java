package com.StartupSAAS.dto.request;

import com.StartupSAAS.enums.Role;
import lombok.Data;

@Data
public class UserRequestDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phone;
    private Role role;
    private Long companyId;
    private String languagePref;
}
