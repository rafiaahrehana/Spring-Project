package com.saas.luminex.service.impl;

import com.saas.luminex.dto.request.PasswordChangeRequest;
import com.saas.luminex.entity.User;
import com.saas.luminex.exception.BadRequestException;
import com.saas.luminex.repository.UserRepository;
import com.saas.luminex.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PasswordService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecurityUtil securityUtil;

    @Transactional
    public void changePassword(PasswordChangeRequest request) {
        User user = securityUtil.getCurrentUser();

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BadRequestException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
}
