package com.startuphub.mapper;

import com.startuphub.dto.response.EmployeeResponse;
import com.startuphub.entity.Department;
import com.startuphub.entity.Employee;
import com.startuphub.entity.User;

public final class EmployeeMapper {

    private EmployeeMapper() {}

    public static EmployeeResponse toResponse(Employee e) {
        User u = e.getUser();
        Department d = e.getDepartment();
        return new EmployeeResponse(
            e.getId(),
            u != null ? u.getId() : null,
            u != null ? u.getFirstName() : null,
            u != null ? u.getLastName() : null,
            u != null ? u.getEmail() : null,
            u != null ? u.getPhone() : null,
            u != null ? u.getImage() : null,
            e.getJobTitle(),
            e.getEmploymentType(),
            e.getGender(),
            e.getDateOfBirth(),
            e.getHireDate(),
            e.getContractEndDate(),
            d != null ? d.getId() : null,
            d != null ? d.getName() : null,
            e.getBasicSalary(),
            e.getHouseRent(),
            e.getMedicalAllowance(),
            e.getTransportAllowance(),
            e.getBankName(),
            e.getEmergencyContactName(),
            e.getEmergencyContactPhone(),
            e.isActive(),
            e.getCreatedAt()
        );
    }
}
