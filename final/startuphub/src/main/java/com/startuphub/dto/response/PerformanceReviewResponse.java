package com.startuphub.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record PerformanceReviewResponse(
    Long id,
    LocalDate reviewPeriodStart,
    LocalDate reviewPeriodEnd,
    Integer scoreWorkQuality,
    Integer scoreProductivity,
    Integer scoreCommunication,
    Integer scoreTeamwork,
    Integer scoreInitiative,
    Integer scorePunctuality,
    Double overallScore,
    String strengths,
    String areasForImprovement,
    String goalsForNextPeriod,
    String comments,
    boolean finalised,
    Long employeeId,
    String employeeName,
    Long reviewedById,
    String reviewedByName,
    LocalDateTime createdAt
) {}
