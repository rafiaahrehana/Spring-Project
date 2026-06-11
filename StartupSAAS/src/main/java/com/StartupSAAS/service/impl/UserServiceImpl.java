package com.StartupSAAS.service.impl;

import com.StartupSAAS.entity.User;
import com.StartupSAAS.enums.Role;
import com.StartupSAAS.repository.UserRepository;
import com.StartupSAAS.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public User createUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public List<User> findAll() {
        return List.of();
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return Optional.empty();
    }

    @Override
    public Optional<User> getUserByRole(Role role) {
        return Optional.empty();
    }

    @Override
    public void deleteUser(Long id) {

    }
}
