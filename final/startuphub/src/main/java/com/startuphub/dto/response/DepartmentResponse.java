package com.startuphub.dto.response;

import java.time.LocalDateTime;

public record DepartmentResponse(
    Long id,
    String name,
    String description,
    boolean active,
    Long headEmployeeId,
    String headEmployeeName,
    long employeeCount,
    LocalDateTime createdAt
) {}
