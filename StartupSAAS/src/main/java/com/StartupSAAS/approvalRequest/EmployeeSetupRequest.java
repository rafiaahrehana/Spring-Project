package com.StartupSAAS.approvalRequest;

import com.StartupSAAS.enums.Designation;
import com.StartupSAAS.enums.Role;
import lombok.Data;

import java.time.LocalDate;

@Data
public class EmployeeSetupRequest {
    private Role role;
    private Designation designation;
    private LocalDate hireDate;
    private Boolean active;
}
