package com.startuphub.dto.request;

import com.startuphub.enums.EmploymentType;
import com.startuphub.enums.Gender;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdateEmployeeRequest(

    @Size(max = 100)
    String jobTitle,

    EmploymentType employmentType,

    Gender gender,

    LocalDate dateOfBirth,

    LocalDate hireDate,

    LocalDate contractEndDate,

    Long departmentId,

    BigDecimal basicSalary,

    BigDecimal houseRent,

    BigDecimal medicalAllowance,

    BigDecimal transportAllowance,

    @Size(max = 100)
    String bankName,

    @Size(max = 100)
    String bankAccountNumber,

    @Size(max = 100)
    String emergencyContactName,

    @Size(max = 30)
    String emergencyContactPhone
) {}
