package com.startuphub.service.impl;

import com.startuphub.dto.request.CreateEmployeeRequest;
import com.startuphub.dto.request.UpdateEmployeeRequest;
import com.startuphub.dto.response.EmployeeResponse;
import com.startuphub.entity.Company;
import com.startuphub.entity.Department;
import com.startuphub.entity.Employee;
import com.startuphub.entity.User;
import com.startuphub.enums.Role;
import com.startuphub.exception.BadRequestException;
import com.startuphub.exception.ResourceNotFoundException;
import com.startuphub.mapper.EmployeeMapper;
import com.startuphub.repository.CompanyRepository;
import com.startuphub.repository.DepartmentRepository;
import com.startuphub.repository.EmployeeRepository;
import com.startuphub.repository.UserRepository;
import com.startuphub.security.SecurityUtil;
import com.startuphub.service.EmailService;
import com.startuphub.service.EmployeeService;
import com.startuphub.service.NotificationPreferenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository           employeeRepository;
    private final UserRepository               userRepository;
    private final CompanyRepository            companyRepository;
    private final DepartmentRepository         departmentRepository;
    private final PasswordEncoder              passwordEncoder;
    private final EmailService                 emailService;
    private final NotificationPreferenceService notificationPreferenceService;
    private final SecurityUtil                 securityUtil;

    @Override
    @Transactional
    public EmployeeResponse create(CreateEmployeeRequest request) {
        Long companyId = requireCompanyId();

        if (userRepository.existsByEmail(request.email())) {
            throw new BadRequestException("An account with this email already exists");
        }

        Company company = companyRepository.findById(companyId)
            .orElseThrow(() -> new ResourceNotFoundException("Company not found"));

        User user = User.builder()
            .firstName(request.firstName())
            .lastName(request.lastName())
            .email(request.email().toLowerCase().trim())
            .password(passwordEncoder.encode(request.password()))
            .role(Role.EMPLOYEE)
            .active(true)
            .emailVerified(true)
            .build();
        userRepository.save(user);

        Employee employee = Employee.builder()
            .user(user)
            .company(company)
            .jobTitle(request.jobTitle())
            .employmentType(request.employmentType())
            .gender(request.gender())
            .dateOfBirth(request.dateOfBirth())
            .hireDate(request.hireDate())
            .contractEndDate(request.contractEndDate())
            .basicSalary(request.basicSalary())
            .houseRent(request.houseRent())
            .medicalAllowance(request.medicalAllowance())
            .transportAllowance(request.transportAllowance())
            .bankName(request.bankName())
            .bankAccountNumber(request.bankAccountNumber())
            .emergencyContactName(request.emergencyContactName())
            .emergencyContactPhone(request.emergencyContactPhone())
            .build();

        if (request.departmentId() != null) {
            Department dept = departmentRepository
                .findByIdAndCompanyId(request.departmentId(), companyId)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Department not found: " + request.departmentId()));
            employee.setDepartment(dept);
        }

        employeeRepository.save(employee);
        notificationPreferenceService.createDefaultsForUser(user.getId());
        emailService.sendWelcomeEmail(user.getEmail(), user.getFirstName(), company.getCompanyName());

        log.info("Employee created: userId={} company={}", user.getId(), companyId);
        return EmployeeMapper.toResponse(employee);
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeResponse getById(Long id) {
        return EmployeeMapper.toResponse(findInTenant(id));
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeResponse getMyProfile() {
        User user = securityUtil.getCurrentUser();
        Employee emp = employeeRepository.findByUserId(user.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Employee profile not found"));
        return EmployeeMapper.toResponse(emp);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EmployeeResponse> listAll(Long departmentId, Pageable pageable) {
        Long companyId = requireCompanyId();
        Page<Employee> page = departmentId != null
            ? employeeRepository.findByCompanyIdAndDepartmentId(companyId, departmentId, pageable)
            : employeeRepository.findByCompanyId(companyId, pageable);
        return page.map(EmployeeMapper::toResponse);
    }

    @Override
    @Transactional
    public EmployeeResponse update(Long id, UpdateEmployeeRequest request) {
        Long companyId = requireCompanyId();
        Employee emp = findInTenant(id);

        if (request.jobTitle()             != null) emp.setJobTitle(request.jobTitle());
        if (request.employmentType()       != null) emp.setEmploymentType(request.employmentType());
        if (request.gender()               != null) emp.setGender(request.gender());
        if (request.dateOfBirth()          != null) emp.setDateOfBirth(request.dateOfBirth());
        if (request.hireDate()             != null) emp.setHireDate(request.hireDate());
        if (request.contractEndDate()      != null) emp.setContractEndDate(request.contractEndDate());
        if (request.basicSalary()          != null) emp.setBasicSalary(request.basicSalary());
        if (request.houseRent()            != null) emp.setHouseRent(request.houseRent());
        if (request.medicalAllowance()     != null) emp.setMedicalAllowance(request.medicalAllowance());
        if (request.transportAllowance()   != null) emp.setTransportAllowance(request.transportAllowance());
        if (request.bankName()             != null) emp.setBankName(request.bankName());
        if (request.bankAccountNumber()    != null) emp.setBankAccountNumber(request.bankAccountNumber());
        if (request.emergencyContactName() != null) emp.setEmergencyContactName(request.emergencyContactName());
        if (request.emergencyContactPhone()!= null) emp.setEmergencyContactPhone(request.emergencyContactPhone());

        if (request.departmentId() != null) {
            Department dept = departmentRepository
                .findByIdAndCompanyId(request.departmentId(), companyId)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Department not found: " + request.departmentId()));
            emp.setDepartment(dept);
        }

        return EmployeeMapper.toResponse(emp);
    }

    @Override
    @Transactional
    public void terminate(Long id) {
        Employee emp = findInTenant(id);
        emp.setActive(false);
        emp.softDelete();

        User user = emp.getUser();
        if (user != null) {
            user.setActive(false);
            user.softDelete();
            userRepository.save(user);
        }
        log.info("Employee terminated: id={}", id);
    }

    // ── Private helpers ───────────────────────────────────────────

    private Employee findInTenant(Long id) {
        return employeeRepository.findByIdAndCompanyId(id, requireCompanyId())
            .orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + id));
    }

    private Long requireCompanyId() {
        Long id = securityUtil.getCurrentCompanyId();
        if (id == null) throw new BadRequestException("No company context");
        return id;
    }
}
