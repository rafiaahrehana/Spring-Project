package com.startuphub.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record JobApplicationRequest(

    @NotBlank(message = "Applicant name is required")
    @Size(max = 150)
    String applicantName,

    @NotBlank(message = "Email is required")
    @Email
    String applicantEmail,

    @Size(max = 30)
    String applicantPhone,

    @Size(max = 500)
    String resumeUrl,

    String coverLetter
) {}
