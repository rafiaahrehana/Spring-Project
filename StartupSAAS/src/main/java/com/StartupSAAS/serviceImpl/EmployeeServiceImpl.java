package com.StartupSAAS.serviceImpl;

import com.StartupSAAS.dto.mapper.EmployeeMapper;
import com.StartupSAAS.dto.request.EmployeeRequest;
import com.StartupSAAS.dto.request.EmployeeSetupRequest;
import com.StartupSAAS.dto.response.EmployeeResponse;
import com.StartupSAAS.entity.Company;
import com.StartupSAAS.entity.Employee;
import com.StartupSAAS.entity.User;
import com.StartupSAAS.enums.Designation;
import com.StartupSAAS.exception.BadRequestException;
import com.StartupSAAS.exception.ResourceNotFoundException;
import com.StartupSAAS.location.entity.Address;
import com.StartupSAAS.location.entity.PoliceStation;
import com.StartupSAAS.location.repository.PoliceStationRepository;
import com.StartupSAAS.repository.CompanyRepository;
import com.StartupSAAS.repository.EmployeeRepository;
import com.StartupSAAS.repository.UserRepository;
import com.StartupSAAS.security.SecurityUtil;
import com.StartupSAAS.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final EmployeeMapper employeeMapper;
    private final PasswordEncoder passwordEncoder;
    private final ImageService imageService;
    private final PoliceStationRepository policeStationRepository;
    private final SecurityUtil securityUtil;

    @Override
    @Transactional
    public EmployeeResponse saveEmployee(Long companyId, EmployeeRequest request, MultipartFile image) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + companyId));

        if (userRepository.existsByEmail(request.getEmail()))
            throw new BadRequestException("Email already registered");

        User user = employeeMapper.toUser(request, passwordEncoder);

        if (image != null && !image.isEmpty())
            user.setImage(imageService.upload(image, "profile", request.getFirstName()));

        if (request.getPoliceStationId() != null) {
            PoliceStation policeStation = policeStationRepository.findById(request.getPoliceStationId())
                    .orElseThrow(() -> new BadRequestException("Police station not found"));
            Address address = new Address();
            address.setHouseNo(request.getHouseNo());
            address.setRoad(request.getRoad());
            address.setPostOffice(request.getPostOffice());
            address.setPoliceStation(policeStation);
            user.setAddress(address);
        }
        userRepository.save(user);

        Employee employee = employeeMapper.toEmployee(request);
        employee.setUser(user);
        employee.setCompany(company);
        employee.setHireDate(LocalDate.now());
        employeeRepository.save(employee);
        return employeeMapper.toResponse(employee);
    }

    @Override
    public EmployeeResponse getEmployeeById(Long id) {
        return employeeMapper.toResponse(
                employeeRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id)));
    }

    @Override
    @Transactional
    public EmployeeResponse updateEmployee(Long id, EmployeeRequest request, MultipartFile image) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));

        User user = employee.getUser();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhone(request.getPhone());
        employee.setDob(request.getDob());
        employee.setGender(request.getGender());

        if (image != null && !image.isEmpty())
            user.setImage(imageService.upload(image, "profile", request.getFirstName()));

        if (request.getPoliceStationId() != null) {
            PoliceStation policeStation = policeStationRepository.findById(request.getPoliceStationId())
                    .orElseThrow(() -> new BadRequestException("Police station not found"));
            Address address = user.getAddress() != null ? user.getAddress() : new Address();
            address.setHouseNo(request.getHouseNo());
            address.setRoad(request.getRoad());
            address.setPostOffice(request.getPostOffice());
            address.setPoliceStation(policeStation);
            user.setAddress(address);
        }
        return employeeMapper.toResponse(employee);
    }

    @Override
    @Transactional
    public EmployeeResponse assignRoleAndDesignation(Long id, EmployeeSetupRequest request) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));

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
        User currentUser = securityUtil.getCurrentUser();
        return companyRepository.findByUserId(currentUser.getId())
                .map(company -> employeeRepository.findByCompanyId(company.getId())
                        .stream()
                        .map(employeeMapper::toResponse)
                        .collect(Collectors.toList()))
                .orElseThrow(() -> new ResourceNotFoundException("Company not found for current user"));
    }

    @Override
    public List<EmployeeResponse> getEmployeesByDesignation(Designation designation) {
        // FIX: scoped to current user's company — was returning from all companies
        User currentUser = securityUtil.getCurrentUser();
        Company company = companyRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Company not found for current user"));

        return employeeRepository.findByCompanyIdAndDesignation(company.getId(), designation)
                .stream()
                .map(employeeMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteEmployee(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));

        if (employee.getUser() != null)
            userRepository.delete(employee.getUser());
        employeeRepository.delete(employee);
    }
}