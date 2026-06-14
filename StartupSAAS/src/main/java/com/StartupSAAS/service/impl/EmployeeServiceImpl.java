package com.StartupSAAS.service.impl;

import com.StartupSAAS.dto.mapper.EmployeeMapper;
import com.StartupSAAS.dto.request.EmployeeRequest;
import com.StartupSAAS.dto.response.EmployeeResponse;
import com.StartupSAAS.entity.Company;
import com.StartupSAAS.entity.User;
import com.StartupSAAS.entity.address.Address;
import com.StartupSAAS.entity.address.PostOffice;
import com.StartupSAAS.enums.Role;
import com.StartupSAAS.exception.BadRequestException;
import com.StartupSAAS.repository.CompanyRepository;
import com.StartupSAAS.repository.UserRepository;
import com.StartupSAAS.repository.location.PostOfficeRepository;
import com.StartupSAAS.service.EmployeeService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final EmployeeMapper employeeMapper;
    private final PasswordEncoder passwordEncoder;
    private final PostOfficeRepository postOfficeRepository;

    @Override
    @Transactional
    public EmployeeResponse createEmployee(Long companyId, EmployeeRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));


        PostOffice postOffice = postOfficeRepository.findByName((request.getPostOffice())
                        .orElseThrow(() -> new RuntimeException("Post Office not found"));


        Address address = new Address();
        address.setHouseNo(request.getHouseNo());
        address.setRoad(request.getRoad());
        address.setPostOffice(postOffice);


        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.EMPLOYEE)
                .designation(request.getDesignation())
                .phone(request.getPhone())
                .company(company)
                .address(address)
                .isActive(true)
                .build();

        return employeeMapper.toResponse(
                userRepository.save(user)
        );
    }
}
