package com.StartupSAAS.service.impl;

import com.StartupSAAS.dto.mapper.EmployeeMapper;
import com.StartupSAAS.dto.request.EmployeeRequest;
import com.StartupSAAS.dto.response.EmployeeResponse;
import com.StartupSAAS.entity.Company;
import com.StartupSAAS.entity.Employee;
import com.StartupSAAS.entity.User;
import com.StartupSAAS.enums.Designation;
import com.StartupSAAS.enums.Role;
import com.StartupSAAS.exception.BadRequestException;
import com.StartupSAAS.exception.ResourceNotFoundException;
import com.StartupSAAS.repository.CompanyRepository;
import com.StartupSAAS.repository.EmployeeRepository;
import com.StartupSAAS.repository.UserRepository;
import com.StartupSAAS.service.EmployeeService;
import jakarta.transaction.Transactional;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;
    private final PasswordEncoder passwordEncoder;

    @Value("${image.upload.dir}")
    private String uploadDir;

    @Override
    @Transactional
    public EmployeeResponse saveEmployee(
            Long companyId, EmployeeRequest empRequest, MultipartFile image) {

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new BadRequestException("Company not found"));

        if (userRepository.existsByEmail(empRequest.getEmail()))
            throw new BadRequestException("Email already exists");

        User user = employeeMapper.toUser(empRequest, company, passwordEncoder);
        if (image != null && !image.isEmpty())
            user.setImage(uploadImage(image, empRequest.getName()));
        userRepository.save(user);

        Employee employee = employeeMapper.toEmployee(empRequest);
        employee.setCompany(company);
        employee.setUser(user);
        employeeRepository.save(employee);

        return employeeMapper.toResponse(employee);
    }

    @Override
    public EmployeeResponse getEmployeeById(Long id) {
             Employee employee = employeeRepository.findById(id)
                        .orElseThrow(() -> new BadRequestException("Employee not found with id: " + id));

        return employeeMapper.toResponse(employee);
    }



    @Override
    public List<EmployeeResponse> getEmployeesByRole(Role role) {
        // Find employees by role
        List<Employee> employees = employeeRepository.findByUser_Role(role);
        if (employees.isEmpty()) {
            throw new ResourceNotFoundException("No employees found with role: " + role);
        }

        List<EmployeeResponse> responses = new java.util.ArrayList<>();
        for (Employee employee : employees) {
            responses.add(employeeMapper.toResponse(employee));
        }

        return responses;
    }

    @Override
    public List<EmployeeResponse> getEmployeesByDesignation(Designation designation) {
        // Find employees by designation
        List<Employee> employees = employeeRepository.findByDesignation(designation);
        if (employees.isEmpty()) {
            throw new ResourceNotFoundException("No employees found with designation: " + designation);
        }

        List<EmployeeResponse> responses = new java.util.ArrayList<>();
        for (Employee employee : employees) {
            responses.add(employeeMapper.toResponse(employee));
        }

        return responses;
    }
    @Override
    public List<EmployeeResponse> getAllEmployees() {
        List<Employee> employees = employeeRepository.findAll();
        if (employees.isEmpty()) {
            throw new ResourceNotFoundException("No employees found");
        }
        return employees.stream()
                .map(employeeMapper::toResponse)
                .collect(Collectors.toList());

    }

    @Override
    @Transactional
    public void deleteEmployee(Long id) {
        Employee employee = employeeRepository.findById(id)
                        .orElseThrow(() -> new BadRequestException("Employee not found with id: " + id));

        if (employee.getUser() != null) {
            userRepository.delete(employee.getUser());
        }
        employeeRepository.delete(employee);
    }

    private String uploadImage(MultipartFile file, String name) {
        try {
            Path path = Paths.get(uploadDir, "employee");
            if (!Files.exists(path)) Files.createDirectories(path);

            String original = file.getOriginalFilename();
            String ext = (original != null && original.contains("."))
                    ? original.substring(original.lastIndexOf("."))
                    : "";

            String fileName = name.trim().replaceAll("\\s+", "_") + "_" + UUID.randomUUID() + ext;
            Files.copy(file.getInputStream(), path.resolve(fileName));

            return fileName;

        } catch (Exception e) {
            throw new RuntimeException("Image upload failed");
        }
    }
}
