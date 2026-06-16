package com.StartupSAAS.service;

import com.StartupSAAS.dto.request.UserCreateRequest;
import com.StartupSAAS.dto.response.UserResponse;
import com.StartupSAAS.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

    UserResponse getMyProfile();

    UserResponse getUserById(Long id);

    // company owner see all users
    Page<UserResponse> getCompanyUsers(Pageable pageable);

    // company owner filter:
    // ADMIN / EMPLOYEE / CLIENT
    Page<UserResponse> getUsersByRole(Role role, Pageable pageable);

    // search user by name
    Page<UserResponse> searchUsers(String name, Pageable pageable);

    UserResponse createUser(UserCreateRequest request, MultipartFile image);

    UserResponse updateProfile(Long id, UserCreateRequest request);

    void toggleUserActive(Long id);

    void deleteUser(Long id);
}
