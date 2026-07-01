package com.startuphub.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record PerformanceReviewRequest(

    @NotNull(message = "Employee ID is required")
    Long employeeId,

    @NotNull
    LocalDate reviewPeriodStart,

    @NotNull
    LocalDate reviewPeriodEnd,

    @Min(1) @Max(5)
    Integer scoreWorkQuality,

    @Min(1) @Max(5)
    Integer scoreProductivity,

    @Min(1) @Max(5)
    Integer scoreCommunication,

    @Min(1) @Max(5)
    Integer scoreTeamwork,

    @Min(1) @Max(5)
    Integer scoreInitiative,

    @Min(1) @Max(5)
    Integer scorePunctuality,

    String strengths,

    String areasForImprovement,

    String goalsForNextPeriod,

    String comments
) {}
