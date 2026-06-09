package com.saas.luminex.service;

import com.saas.luminex.dto.response.UserResponse;
import com.saas.luminex.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface UserService {
    UserResponse getMyProfile();
    UserResponse getUserById(Long id);
    Page<UserResponse> getUsersByRole(Role role, Pageable pageable);
    UserResponse updateProfile(Long id, Map<String, Object> updates);
    UserResponse createUser(String name, String email, String password, Role role,
                            String companyName, String address, String phone);
    void toggleUserActive(Long id);
    void deleteUser(Long id);
}
