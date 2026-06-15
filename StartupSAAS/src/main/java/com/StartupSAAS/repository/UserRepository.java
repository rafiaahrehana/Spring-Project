package com.StartupSAAS.repository;

import com.StartupSAAS.entity.User;
import com.StartupSAAS.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Page<User> findByRole(Role role, Pageable pageable);

    List<User> findByCompanyIdAndRole(Long companyId, Role role);

    @Query("SELECT COUNT(u) FROM User u WHERE u.role = :role AND u.isActive = true")
    long countActiveByRole(Role role);

    Page<User> findByRoleAndNameContainingIgnoreCase(Role role, String name, Pageable pageable);

}
