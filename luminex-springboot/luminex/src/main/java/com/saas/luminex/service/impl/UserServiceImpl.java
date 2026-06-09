package com.saas.luminex.service.impl;

import com.saas.luminex.dto.response.UserResponse;
import com.saas.luminex.entity.User;
import com.saas.luminex.enums.Role;
import com.saas.luminex.exception.BadRequestException;
import com.saas.luminex.exception.ResourceNotFoundException;
import com.saas.luminex.repository.UserRepository;
import com.saas.luminex.service.UserService;
import com.saas.luminex.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecurityUtil securityUtil;

    @Override
    @Transactional(readOnly = true)
    public UserResponse getMyProfile() {
        return mapToResponse(securityUtil.getCurrentUser());
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        return mapToResponse(findUser(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> getUsersByRole(Role role, Pageable pageable) {
        return userRepository.findByRole(role, pageable).map(this::mapToResponse);
    }

    @Override
    @Transactional
    public UserResponse updateProfile(Long id, Map<String, Object> updates) {
        User user = findUser(id);
        if (updates.containsKey("name"))        user.setName((String) updates.get("name"));
        if (updates.containsKey("phone"))       user.setPhone((String) updates.get("phone"));
        if (updates.containsKey("address"))     user.setAddress((String) updates.get("address"));
        if (updates.containsKey("companyName")) user.setCompanyName((String) updates.get("companyName"));
        if (updates.containsKey("profileImageUrl")) user.setProfileImageUrl((String) updates.get("profileImageUrl"));
        if (updates.containsKey("password")) {
            String raw = (String) updates.get("password");
            if (raw != null && raw.length() >= 6) {
                user.setPassword(passwordEncoder.encode(raw));
            }
        }
        return mapToResponse(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserResponse createUser(String name, String email, String password, Role role,
                                   String companyName, String address, String phone) {
        if (userRepository.existsByEmail(email)) {
            throw new BadRequestException("Email already exists: " + email);
        }
        User user = User.builder()
                .name(name)
                .email(email)
                .password(passwordEncoder.encode(password))
                .role(role)
                .companyName(companyName)
                .address(address)
                .phone(phone)
                .isActive(true)
                .build();
        return mapToResponse(userRepository.save(user));
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
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User", id);
        }
        userRepository.deleteById(id);
    }

    // --- Mapper ---
    public UserResponse mapToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .companyName(user.getCompanyName())
                .address(user.getAddress())
                .phone(user.getPhone())
                .profileImageUrl(user.getProfileImageUrl())
                .isActive(user.isActive())
                .createdAt(user.getCreatedAt())
                .build();
    }

    private User findUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
    }
}
