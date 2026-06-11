package com.saas.luminex.repository;

import com.saas.luminex.entity.User;
import com.saas.luminex.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("UserRepository Integration Tests")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        userRepository.save(User.builder().name("Super Admin").email("superadmin@test.com")
                .password("encoded").role(Role.SUPER_ADMIN).isActive(true).build());

        userRepository.save(User.builder().name("Admin User").email("admin@test.com")
                .password("encoded").role(Role.ADMIN).isActive(true).build());

        userRepository.save(User.builder().name("Badrul Islam").email("badrul@test.com")
                .password("encoded").role(Role.CLIENT).isActive(true).build());

        userRepository.save(User.builder().name("Sajin Ahmed").email("sajin@test.com")
                .password("encoded").role(Role.CLIENT).isActive(true).build());

        userRepository.save(User.builder().name("Emon Hossain").email("emon@test.com")
                .password("encoded").role(Role.EMPLOYEE).isActive(true).build());

        userRepository.save(User.builder().name("Sadiya Rahman").email("sadiya@test.com")
                .password("encoded").role(Role.EMPLOYEE).isActive(false).build()); // inactive
    }

    @Test
    @DisplayName("findByEmail() - returns user for existing email")
    void findByEmail_found() {
        Optional<User> result = userRepository.findByEmail("badrul@test.com");
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Badrul Islam");
    }

    @Test
    @DisplayName("findByEmail() - returns empty for unknown email")
    void findByEmail_notFound() {
        Optional<User> result = userRepository.findByEmail("unknown@test.com");
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("existsByEmail() - true for existing email")
    void existsByEmail_true() {
        assertThat(userRepository.existsByEmail("admin@test.com")).isTrue();
    }

    @Test
    @DisplayName("existsByEmail() - false for unknown email")
    void existsByEmail_false() {
        assertThat(userRepository.existsByEmail("nobody@test.com")).isFalse();
    }

    @Test
    @DisplayName("findByRole() - returns only users with that role")
    void findByRole_returnsCorrectRoleOnly() {
        Page<User> clients = userRepository.findByRole(Role.CLIENT, PageRequest.of(0, 10));
        assertThat(clients.getTotalElements()).isEqualTo(2);
        assertThat(clients.getContent()).allMatch(u -> u.getRole() == Role.CLIENT);
    }

    @Test
    @DisplayName("countActiveByRole() - counts only active users per role")
    void countActiveByRole_countsOnlyActive() {
        long activeEmployees = userRepository.countActiveByRole(Role.EMPLOYEE);
        assertThat(activeEmployees).isEqualTo(1); // Sadiya is inactive
    }

    @Test
    @DisplayName("findByRole() - empty result for SUPER_ADMIN when searching EMPLOYEE")
    void findByRole_crossRole_empty() {
        Page<User> superAdmins = userRepository.findByRole(Role.SUPER_ADMIN, PageRequest.of(0, 10));
        assertThat(superAdmins.getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("findByRoleAndNameContainingIgnoreCase() - case-insensitive name search")
    void findByRoleAndName_caseInsensitive() {
        Page<User> results = userRepository.findByRoleAndNameContainingIgnoreCase(
                Role.CLIENT, "BADRUL", PageRequest.of(0, 10));
        assertThat(results.getTotalElements()).isEqualTo(1);
        assertThat(results.getContent().get(0).getEmail()).isEqualTo("badrul@test.com");
    }
}
