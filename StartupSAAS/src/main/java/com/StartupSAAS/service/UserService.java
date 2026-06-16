package com.StartupSAAS.service;

import com.StartupSAAS.dto.request.PasswordChangeRequest;
import com.StartupSAAS.dto.request.UserRequest;
import com.StartupSAAS.dto.response.UserResponse;
import com.StartupSAAS.enums.Role;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

    // any logged in user
    UserResponse getMyProfile();
    UserResponse updateMyProfile(UserRequest request, MultipartFile image);
    void changePassword(PasswordChangeRequest request);

    // super admin only
    UserResponse getUserById(Long id);
    List<UserResponse> getUsersByRole(Role role);
    void toggleUserActive(Long id);
    void deleteUser(Long id);
}