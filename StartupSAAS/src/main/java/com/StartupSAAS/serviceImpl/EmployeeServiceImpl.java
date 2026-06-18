package com.StartupSAAS.serviceImpl;

import com.StartupSAAS.dto.mapper.EmployeeMapper;
import com.StartupSAAS.approvalRequest.EmployeeSetupRequest;
import com.StartupSAAS.dto.request.EmployeeRequest;
import com.StartupSAAS.dto.response.EmployeeResponse;
import com.StartupSAAS.entity.Company;
import com.StartupSAAS.entity.Employee;
import com.StartupSAAS.entity.User;
import com.StartupSAAS.enums.Designation;
import com.StartupSAAS.exception.BadRequestException;
import com.StartupSAAS.exception.ResourceNotFoundException;
import com.StartupSAAS.location.entity.Address;
import com.StartupSAAS.location.entity.PostOffice;
import com.StartupSAAS.location.repository.*;
import com.StartupSAAS.repository.CompanyRepository;
import com.StartupSAAS.repository.EmployeeRepository;
import com.StartupSAAS.repository.UserRepository;
import com.StartupSAAS.service.EmployeeService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;
    private final PasswordEncoder passwordEncoder;
    private final ImageService imageService;
    private final PostOfficeRepository postOfficeRepository;

    @Override
    @Transactional
    public EmployeeResponse saveEmployee(Long companyId, EmployeeRequest request, MultipartFile image) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new BadRequestException("Company not found"));

        if (userRepository.existsByEmail(request.getEmail()))
            throw new BadRequestException("Email already exists");

        Address address = null;
        if (request.getPostOfficeId() != null) {
            PostOffice postOffice = postOfficeRepository.findById(request.getPostOfficeId())
                    .orElseThrow(() -> new BadRequestException("Post office not found"));

            address = new Address();
            address.setHouseNo(request.getHouseNo());
            address.setRoad(request.getRoad());
            address.setPostOffice(postOffice);
        }

        User user = employeeMapper.toUser(request, passwordEncoder);
        user.setAddress(address);
        if (image != null && !image.isEmpty())
            user.setImage(imageService.upload(image, "employee", request.getName()));
        userRepository.save(user);

        Employee employee = employeeMapper.toEmployee(request);
        employee.setCompany(company);
        employee.setUser(user);
        employeeRepository.save(employee);

        return employeeMapper.toResponse(employee);
    }

    @Override
    public EmployeeResponse getEmployeeById(Long id) {
        return employeeMapper.toResponse(
                employeeRepository.findById(id)
                        .orElseThrow(() -> new BadRequestException("Employee not found with id: " + id)));
    }

    @Override
    @Transactional
    public EmployeeResponse updateEmployee(Long id, EmployeeRequest request, MultipartFile image) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Employee not found with id: " + id));

        User user = employee.getUser();
        user.setPhone(request.getPhone());
        user.setFirstName(request.getName());
        if (image != null && !image.isEmpty())
            user.setImage(imageService.upload(image, "employee", request.getName()));

        employee.setDob(request.getDob());
        employee.setGender(request.getGender());

        return employeeMapper.toResponse(employee);
    }

    @Override
    @Transactional
    public EmployeeResponse assignRoleAndDesignation(Long id, EmployeeSetupRequest request) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Employee not found with id: " + id));

        if (request.getDesignation() != null)
            employee.setDesignation(request.getDesignation());

        if (request.getHireDate() != null)
            employee.setHireDate(request.getHireDate());

        if (request.getRole() != null)
            employee.getUser().setRole(request.getRole());

        if (request.getActive() != null)
            employee.getUser().setActive(request.getActive());

        return employeeMapper.toResponse(employee);
    }

    @Override
    public List<EmployeeResponse> getAllEmployees() {
        return employeeRepository.findAll().stream()
                .map(employeeMapper::toResponse)
                .collect(Collectors.toList());
    }


    @Override
    public List<EmployeeResponse> getEmployeesByDesignation(Designation designation) {
        List<Employee> employees = employeeRepository.findByDesignation(designation);
        if (employees.isEmpty())
            throw new ResourceNotFoundException("No employees found with designation: " + designation);
        return employees.stream()
                .map(employeeMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteEmployee(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Employee not found with id: " + id));
        if (employee.getUser() != null)
            userRepository.delete(employee.getUser());
        employeeRepository.delete(employee);
    }
}