package com.StartupSAAS.service.impl;

import com.StartupSAAS.dto.mapper.UserMapper;
import com.StartupSAAS.dto.request.UserCreateRequest;
import com.StartupSAAS.dto.response.UserResponse;
import com.StartupSAAS.entity.Company;
import com.StartupSAAS.entity.User;
import com.StartupSAAS.entity.address.Address;
import com.StartupSAAS.entity.address.PostOffice;
import com.StartupSAAS.enums.Role;
import com.StartupSAAS.exception.BadRequestException;
import com.StartupSAAS.exception.ResourceNotFoundException;
import com.StartupSAAS.repository.UserRepository;
import com.StartupSAAS.repository.location.AddressRepository;
import com.StartupSAAS.repository.location.PostOfficeRepository;
import com.StartupSAAS.service.UserService;
import com.StartupSAAS.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final AddressRepository addressRepository;

    private final PostOfficeRepository postOfficeRepository;

    private final PasswordEncoder passwordEncoder;

    private final SecurityUtil securityUtil;

    @Override
    @Transactional
    public UserResponse createUser(UserCreateRequest request, MultipartFile image) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already exists");
        }

        Company company = securityUtil.getCurrentUser().getCompany();

        Address address = null;

        if (request.getPostOfficeId() != null) {

            PostOffice postOffice =
                    postOfficeRepository
                            .findById(request.getPostOfficeId())
                            .orElseThrow(() -> new RuntimeException("Post office not found"));

            address = new Address();
            address.setHouseNo(request.getHouseNo());
            address.setRoad(request.getRoad());
            address.setPostOffice(postOffice);
            addressRepository.save(address);
        }

        User user =
                User.builder()
                        .name(request.getName())
                        .email(request.getEmail())
                        .password(passwordEncoder.encode(request.getPassword()))
                        .phone(request.getPhone())
                        .role(request.getRole())
                        .company(company)
                        .address(address)
                        .isActive(true)
                        .build();

        return UserMapper.toDTO(userRepository.save(user));
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getMyProfile() {

        return UserMapper.toDTO(securityUtil.getCurrentUser());
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {

        Long companyId = securityUtil.getCurrentUser().getCompany().getId();

        User user =
                userRepository
                        .findByIdAndCompanyId(id, companyId)
                        .orElseThrow(() -> new ResourceNotFoundException("User", id));

        return UserMapper.toDTO(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> getUsersByRole(Role role, Pageable pageable) {

        Long companyId = securityUtil.getCurrentUser().getCompany().getId();

        return userRepository.findByCompanyIdAndRole(companyId, role, pageable).map(UserMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> getCompanyUsers(Pageable pageable) {

        Long companyId = securityUtil.getCurrentUser().getCompany().getId();

        return userRepository.findByCompanyId(companyId, pageable).map(UserMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> searchUsers(String name, Pageable pageable) {

        Long companyId = securityUtil.getCurrentUser().getCompany().getId();

        return userRepository
                .findByCompanyIdAndNameContainingIgnoreCase(companyId, name, pageable)
                .map(UserMapper::toDTO);
    }

    @Override
    @Transactional
    public UserResponse updateProfile(Long id, UserCreateRequest request) {

        User user = findUser(id);

        user.setName(request.getName());

        user.setPhone(request.getPhone());

        return UserMapper.toDTO(userRepository.save(user));
    }

    @Override
    @Transactional
    public void toggleUserActive(Long id) {

        User user = findUser(id);

        user.setActive(!user.isActive());

        userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {

        User user = findUser(id);

        userRepository.delete(user);
    }

    private User findUser(Long id) {

        Long companyId = securityUtil.getCurrentUser().getCompany().getId();

        return userRepository
                .findByIdAndCompanyId(id, companyId)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
    }
}
