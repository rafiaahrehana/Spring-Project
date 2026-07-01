package com.startuphub.mapper;

import com.startuphub.dto.response.DepartmentResponse;
import com.startuphub.entity.Department;
import com.startuphub.entity.Employee;

public final class DepartmentMapper {

    private DepartmentMapper() {}

    public static DepartmentResponse toResponse(Department d) {
        Employee head = d.getHead();
        return new DepartmentResponse(
            d.getId(),
            d.getName(),
            d.getDescription(),
            d.isActive(),
            head != null ? head.getId() : null,
            head != null && head.getUser() != null ? head.getUser().getFullName() : null,
            d.getEmployees() != null ? d.getEmployees().size() : 0L,
            d.getCreatedAt()
        );
    }
}
