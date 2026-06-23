package com.StartupSAAS.dto.mapper;

import com.StartupSAAS.dto.request.EmployeeRequest;
import com.StartupSAAS.dto.response.EmployeeResponse;
import com.StartupSAAS.entity.Employee;
import com.StartupSAAS.entity.User;
import com.StartupSAAS.enums.Role;
import com.StartupSAAS.location.mapper.AddressMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class EmployeeMapper {

    public User toUser(EmployeeRequest request, PasswordEncoder encoder) {
        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPassword(encoder.encode(request.getPassword()));
        user.setRole(Role.EMPLOYEE);
        user.setActive(true);
        return user;
    }

    public Employee toEmployee(EmployeeRequest request) {
        Employee employee = new Employee();
        employee.setDob(request.getDob());
        employee.setGender(request.getGender());
        return employee;
    }

    public EmployeeResponse toResponse(Employee employee) {
        EmployeeResponse response = new EmployeeResponse();
        response.setId(employee.getId());
        response.setDesignation(employee.getDesignation());
        response.setDob(employee.getDob());
        response.setGender(employee.getGender());

        if (employee.getUser() != null) {
            User user = employee.getUser();
            response.setName(user.getFirstName());
            response.setEmail(user.getEmail());
            response.setPhone(user.getPhone());
            response.setImage(user.getImage());
            response.setRole(user.getRole());
            response.setActive(user.isActive());

            if (user.getAddress() != null)
                response.setAddress(AddressMapper.toDTO(user.getAddress()));
        }

        if (employee.getCompany() != null) {
            response.setCompanyId(employee.getCompany().getId());
            response.setCompanyName(employee.getCompany().getCompanyName());
        }

        return response;
    }
}