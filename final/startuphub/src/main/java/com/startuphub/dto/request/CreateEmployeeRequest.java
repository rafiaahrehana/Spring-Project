package com.startuphub.dto.request;

import com.startuphub.enums.EmploymentType;
import com.startuphub.enums.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateEmployeeRequest(

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50)
    String firstName,

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50)
    String lastName,

    @NotBlank(message = "Email is required")
    @Email
    String email,

    @NotBlank(message = "Password is required")
    @Size(min = 8)
    String password,

    @Size(max = 100)
    String jobTitle,

    @NotNull(message = "Employment type is required")
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
