package com.StartupSAAS.service;

import com.StartupSAAS.entity.User;
import com.StartupSAAS.enums.Role;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User createUser(User user);
    List<User> findAll();
    Optional<User> getUserById(Long id);
    Optional<User> getUserByRole(Role role);

    void deleteUser(Long id);
}
