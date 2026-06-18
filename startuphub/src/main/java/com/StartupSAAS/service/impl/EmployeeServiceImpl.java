package com.StartupSAAS.service.impl;

import com.StartupSAAS.dto.mapper.EmployeeMapper;
import com.StartupSAAS.dto.request.EmployeeRequestDTO;
import com.StartupSAAS.dto.response.EmployeeResponseDTO;
import com.StartupSAAS.entity.Company;
import com.StartupSAAS.entity.Employee;
import com.StartupSAAS.entity.User;
import com.StartupSAAS.entity.address.Address;
import com.StartupSAAS.entity.address.PostOffice;
import com.StartupSAAS.enums.Role;
import com.StartupSAAS.repository.CompanyRepository;
import com.StartupSAAS.repository.EmployeeRepository;
import com.StartupSAAS.repository.UserRepository;
import com.StartupSAAS.repository.location.AddressRepository;
import com.StartupSAAS.repository.location.PostOfficeRepository;
import com.StartupSAAS.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final PostOfficeRepository postOfficeRepository;
    private final AddressRepository addressRepository;

    @Value("${image.upload.dir}")
    private String uploadDir;

    @Transactional
    @Override
    public EmployeeResponseDTO create(EmployeeRequestDTO dto, MultipartFile image) {

        // 1. Validate company exists
        Company company = companyRepository.findById(dto.getCompanyId())
                .orElseThrow(() -> new RuntimeException(
                        "Company not found with id: " + dto.getCompanyId()));

        // 2. Validate email not already used
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email already in use: " + dto.getEmail());
        }

        // 3. Create User account with EMPLOYEE role
        User user = new User();
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail().toLowerCase().trim());
        user.setPassword(dto.getPassword()); // BCrypt in security layer
        user.setPhone(dto.getPhone());
        user.setRole(Role.EMPLOYEE);
        user.setCompany(company);
        user.setIsActive(true);
        user.setEmailEnabled(true);
        user.setSmsEnabled(false);

        User savedUser = userRepository.save(user);

        // 4. Build Address from postOfficeId + street
        Address address = null;
        if (dto.getPostOfficeId() != null && dto.getStreet() != null) {
            PostOffice postOffice = postOfficeRepository.findByIdWithDetails(dto.getPostOfficeId())
                    .orElseThrow(() -> new RuntimeException(
                            "PostOffice not found with id: " + dto.getPostOfficeId()));

            address = new Address();
            address.setStreet(dto.getStreet());
            address.setPostOffice(postOffice);
            address = addressRepository.save(address);
        }

        // 5. Create Employee profile
        Employee employee = new Employee();
        employee.setUser(savedUser);
        employee.setCompany(company);
        employee.setDesignation(dto.getDesignation());
        employee.setDepartment(dto.getDepartment());
        employee.setNidNumber(dto.getNidNumber());
        employee.setEmergencyContact(dto.getEmergencyContact());
        employee.setAddress(address);
        employee.setActive(true);

        if (image != null && !image.isEmpty()) {
            employee.setImage(uploadImage(image, dto.getFirstName(), "employee"));
        }

        Employee saved = employeeRepository.save(employee);
        return EmployeeMapper.toDTO(
                employeeRepository.findByIdWithDetails(saved.getId()).orElse(saved));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeResponseDTO> getAll() {
        return employeeRepository.findAllWithDetails()
                .stream().map(EmployeeMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeResponseDTO getById(Long id) {
        return EmployeeMapper.toDTO(
                employeeRepository.findByIdWithDetails(id)
                        .orElseThrow(() -> new RuntimeException(
                                "Employee not found with id: " + id)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeResponseDTO> getByCompany(Long companyId) {
        return employeeRepository.findByCompanyId(companyId)
                .stream().map(EmployeeMapper::toDTO).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public EmployeeResponseDTO update(Long id, EmployeeRequestDTO dto, MultipartFile image) {

        Employee employee = employeeRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new RuntimeException(
                        "Employee not found with id: " + id));

        // Update User fields
        User user = employee.getUser();
        if (dto.getFirstName() != null)  user.setFirstName(dto.getFirstName());
        if (dto.getLastName() != null)   user.setLastName(dto.getLastName());
        if (dto.getPhone() != null)      user.setPhone(dto.getPhone());

        // Email change: validate uniqueness
        if (dto.getEmail() != null
                && !dto.getEmail().equalsIgnoreCase(user.getEmail())) {
            if (userRepository.existsByEmail(dto.getEmail())) {
                throw new RuntimeException("Email already in use: " + dto.getEmail());
            }
            user.setEmail(dto.getEmail().toLowerCase().trim());
        }
        userRepository.save(user);

        // Update Employee profile fields
        if (dto.getDesignation() != null)     employee.setDesignation(dto.getDesignation());
        if (dto.getDepartment() != null)      employee.setDepartment(dto.getDepartment());
        if (dto.getNidNumber() != null)       employee.setNidNumber(dto.getNidNumber());
        if (dto.getEmergencyContact() != null) employee.setEmergencyContact(dto.getEmergencyContact());

        // Update address if new postOfficeId + street provided
        if (dto.getPostOfficeId() != null && dto.getStreet() != null) {
            PostOffice postOffice = postOfficeRepository.findByIdWithDetails(dto.getPostOfficeId())
                    .orElseThrow(() -> new RuntimeException(
                            "PostOffice not found with id: " + dto.getPostOfficeId()));

            Address address = employee.getAddress();
            if (address == null) {
                address = new Address();
            }
            address.setStreet(dto.getStreet());
            address.setPostOffice(postOffice);
            employee.setAddress(addressRepository.save(address));
        }

        // Reassign company if changed
        if (dto.getCompanyId() != null) {
            Company company = companyRepository.findById(dto.getCompanyId())
                    .orElseThrow(() -> new RuntimeException("Company not found"));
            employee.setCompany(company);
        }

        if (image != null && !image.isEmpty()) {
            employee.setImage(uploadImage(image, user.getFirstName(), "employee"));
        }

        Employee saved = employeeRepository.save(employee);
        return EmployeeMapper.toDTO(
                employeeRepository.findByIdWithDetails(saved.getId()).orElse(saved));
    }

    @Override
    public void delete(Long id) {
        employeeRepository.deleteById(id);
    }

    private String uploadImage(MultipartFile file, String name, String folder) {
        try {
            Path path = Paths.get(uploadDir, folder);
            if (!Files.exists(path)) Files.createDirectories(path);

            String ext = "";
            String original = file.getOriginalFilename();
            if (original != null && original.contains("."))
                ext = original.substring(original.lastIndexOf("."));

            String fileName = name.trim().replaceAll("\\s+", "_")
                    + "_" + UUID.randomUUID() + ext;
            Files.copy(file.getInputStream(), path.resolve(fileName));
            return fileName;
        } catch (Exception e) {
            throw new RuntimeException("Image upload failed: " + e.getMessage());
        }
    }
}
