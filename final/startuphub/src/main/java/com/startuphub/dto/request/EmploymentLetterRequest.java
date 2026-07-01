package com.startuphub.dto.request;

import com.startuphub.enums.LetterType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record EmploymentLetterRequest(

    @NotNull(message = "Employee ID is required")
    Long employeeId,

    @NotNull(message = "Letter type is required")
    LetterType letterType,

    @Size(max = 100)
    String referenceNumber,

    @NotNull
    LocalDate issueDate,

    @NotBlank(message = "Letter content is required")
    String content,

    @Size(max = 150)
    String signedBy
) {}
