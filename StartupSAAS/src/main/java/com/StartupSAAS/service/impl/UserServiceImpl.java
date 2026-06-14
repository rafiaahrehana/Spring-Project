package com.StartupSAAS.service.impl;
import com.StartupSAAS.dto.mapper.UserMapper;
import com.StartupSAAS.dto.response.UserResponse;
import com.StartupSAAS.entity.User;
import com.StartupSAAS.enums.Role;
import com.StartupSAAS.exception.BadRequestException;
import com.StartupSAAS.exception.ResourceNotFoundException;
import com.StartupSAAS.repository.UserRepository;
import com.StartupSAAS.service.UserService;
import com.StartupSAAS.util.SecurityUtil;
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
    private final SecurityUtil securityUtil;


    @Override
    @Transactional
    public UserResponse createUser(String name, String email, String password, Role role, String companyName, String address, String phone) {
        if (userRepository.existsByEmail(email)) {
            throw new BadRequestException("Email already exists");
        }
        User user = User.builder()
                .name(name)
                .email(email)
                .password(password)
                .role(role)
                .phone(phone)
                .isActive(true)
                .build();
        return UserMapper.toDTO(
                userRepository.save(user)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getMyProfile() {
        return UserMapper.toDTO(securityUtil.getCurrentUser());
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        return UserMapper.toDTO(findUser(id));
    }


    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> getUsersByRole(Role role, Pageable pageable) {
        return userRepository.findByRole(role, pageable).map(UserMapper::toDTO);
    }

    @Override
    @Transactional
    public UserResponse updateProfile(Long id, Map<String, Object> updates) {
        User user = findUser(id);
        user.setName((String) updates.get("name"));
        user.setPhone((String) updates.get("phone"));
        user.setProfileImageUrl((String) updates.get("profileImageUrl"));
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
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User", id);
        }
        userRepository.deleteById(id);
    }

    private User findUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
    }
}
