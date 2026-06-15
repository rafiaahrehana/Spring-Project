package com.StartupSAAS.service;
import com.StartupSAAS.dto.request.UserCreateRequest;
import com.StartupSAAS.dto.response.UserResponse;
import com.StartupSAAS.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface UserService {
    UserResponse getMyProfile();

    UserResponse getUserById(Long id);

    Page<UserResponse> getUsersByRole(Role role, Pageable pageable);

    UserResponse updateProfile(Long id, Map <String, Object> updates);

    UserResponse createUser(UserCreateRequest request);

    void toggleUserActive(Long id);

    void deleteUser(Long id);
}
