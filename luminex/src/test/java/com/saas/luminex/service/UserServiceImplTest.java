package com.saas.luminex.service;

import com.saas.luminex.dto.response.UserResponse;
import com.saas.luminex.entity.User;
import com.saas.luminex.enums.Role;
import com.saas.luminex.exception.BadRequestException;
import com.saas.luminex.exception.ResourceNotFoundException;
import com.saas.luminex.repository.UserRepository;
import com.saas.luminex.service.impl.UserServiceImpl;
import com.saas.luminex.util.SecurityUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Unit Tests")
class UserServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private SecurityUtil securityUtil;

    @InjectMocks
    private UserServiceImpl userService;

    private User mockClient;
    private User mockEmployee;

    @BeforeEach
    void setUp() {
        mockClient = User.builder()
                .id(1L).name("Badrul Islam").email("badrul@test.com")
                .password("encoded").role(Role.CLIENT).isActive(true).build();

        mockEmployee = User.builder()
                .id(2L).name("Emon Hossain").email("emon@test.com")
                .password("encoded").role(Role.EMPLOYEE).isActive(true).build();
    }

    // ─── Get Profile ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("getMyProfile() - returns current user's profile")
    void getMyProfile_returnsCurrentUser() {
        when(securityUtil.getCurrentUser()).thenReturn(mockClient);

        UserResponse response = userService.getMyProfile();

        assertThat(response.getEmail()).isEqualTo("badrul@test.com");
        assertThat(response.getRole()).isEqualTo(Role.CLIENT);
    }

    // ─── Get By ID ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("getUserById() - existing ID returns UserResponse")
    void getUserById_found() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockClient));

        UserResponse response = userService.getUserById(1L);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("Badrul Islam");
    }

    @Test
    @DisplayName("getUserById() - missing ID throws ResourceNotFoundException")
    void getUserById_notFound_throws() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ─── Get By Role ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("getUsersByRole() - returns paged results for role")
    void getUsersByRole_returnsPaged() {
        PageRequest pageable = PageRequest.of(0, 10);
        Page<User> page = new PageImpl<>(List.of(mockClient, mockEmployee));
        when(userRepository.findByRole(Role.CLIENT, pageable)).thenReturn(page);

        Page<UserResponse> result = userService.getUsersByRole(Role.CLIENT, pageable);

        assertThat(result.getTotalElements()).isEqualTo(2);
    }

    // ─── Update Profile ───────────────────────────────────────────────────────

    @Test
    @DisplayName("updateProfile() - updates allowed fields only")
    void updateProfile_updatesFields() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockClient));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        Map<String, Object> updates = Map.of("name", "Badrul Updated", "phone", "01700000000");
        UserResponse response = userService.updateProfile(1L, updates);

        assertThat(response.getName()).isEqualTo("Badrul Updated");
        assertThat(response.getPhone()).isEqualTo("01700000000");
    }

    @Test
    @DisplayName("updateProfile() - password shorter than 6 chars is ignored")
    void updateProfile_shortPasswordIgnored() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockClient));
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        userService.updateProfile(1L, Map.of("password", "abc"));

        verify(passwordEncoder, never()).encode(anyString());
    }

    // ─── Create User ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("createUser() - creates employee with correct role")
    void createUser_employee_success() {
        when(userRepository.existsByEmail("newemployee@test.com")).thenReturn(false);
        when(passwordEncoder.encode("pass123")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u = User.builder().id(3L).name(u.getName()).email(u.getEmail())
                    .role(u.getRole()).password(u.getPassword()).isActive(true).build();
            return u;
        });

        UserResponse response = userService.createUser(
                "New Employee", "newemployee@test.com", "pass123",
                Role.EMPLOYEE, null, null, null);

        assertThat(response.getRole()).isEqualTo(Role.EMPLOYEE);
        assertThat(response.getEmail()).isEqualTo("newemployee@test.com");
    }

    @Test
    @DisplayName("createUser() - duplicate email throws BadRequestException")
    void createUser_duplicateEmail_throws() {
        when(userRepository.existsByEmail("emon@test.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.createUser(
                "Emon", "emon@test.com", "pass", Role.EMPLOYEE, null, null, null))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("already exists");
    }

    // ─── Toggle Active ────────────────────────────────────────────────────────

    @Test
    @DisplayName("toggleUserActive() - active user becomes inactive")
    void toggleUserActive_activeBecomesFalse() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockClient));
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        userService.toggleUserActive(1L);

        verify(userRepository).save(argThat(u -> !u.isActive()));
    }

    @Test
    @DisplayName("toggleUserActive() - inactive user becomes active")
    void toggleUserActive_inactiveBecomesTure() {
        mockClient.setActive(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockClient));
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        userService.toggleUserActive(1L);

        verify(userRepository).save(argThat(User::isActive));
    }
}
