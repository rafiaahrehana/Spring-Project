package com.saas.luminex.config;

import com.saas.luminex.entity.User;
import com.saas.luminex.enums.Role;
import com.saas.luminex.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seedSuperAdmin();
    }

    private void seedSuperAdmin() {
        String superAdminEmail = "superadmin@luminex.com";
        if (userRepository.existsByEmail(superAdminEmail)) {
            return; // Already seeded
        }

        User superAdmin = User.builder()
                .name("Super Admin")
                .email(superAdminEmail)
                .password(passwordEncoder.encode("superadmin123"))
                .role(Role.SUPER_ADMIN)
                .companyName("LumiNex")
                .isActive(true)
                .build();
        userRepository.save(superAdmin);
        log.info("✅ SuperAdmin seeded: {}", superAdminEmail);

        // Seed a default Admin
        String adminEmail = "admin@luminex.com";
        if (!userRepository.existsByEmail(adminEmail)) {
            User admin = User.builder()
                    .name("Admin User")
                    .email(adminEmail)
                    .password(passwordEncoder.encode("admin123"))
                    .role(Role.ADMIN)
                    .companyName("LumiNex")
                    .isActive(true)
                    .build();
            userRepository.save(admin);
            log.info("✅ Admin seeded: {}", adminEmail);
        }
    }
}
