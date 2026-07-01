package com.startuphub.dto.response;

import com.startuphub.enums.EmploymentType;
import com.startuphub.enums.Gender;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record EmployeeResponse(
    Long id,
    Long userId,
    String firstName,
    String lastName,
    String email,
    String phone,
    String image,
    String jobTitle,
    EmploymentType employmentType,
    Gender gender,
    LocalDate dateOfBirth,
    LocalDate hireDate,
    LocalDate contractEndDate,
    Long departmentId,
    String departmentName,
    BigDecimal basicSalary,
    BigDecimal houseRent,
    BigDecimal medicalAllowance,
    BigDecimal transportAllowance,
    String bankName,
    String emergencyContactName,
    String emergencyContactPhone,
    boolean active,
    LocalDateTime createdAt
) {}
