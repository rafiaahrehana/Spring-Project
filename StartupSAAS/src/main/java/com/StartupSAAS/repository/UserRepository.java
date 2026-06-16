package com.StartupSAAS.repository;

import com.StartupSAAS.entity.User;
import com.StartupSAAS.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    // all company users
    Page<User> findByCompanyId(Long companyId, Pageable pageable);

    // users by company and role
    Page<User> findByCompanyIdAndRole(Long companyId, Role role, Pageable pageable);

    // search user by name in company
    Page<User> findByCompanyIdAndNameContainingIgnoreCase(
            Long companyId, String name, Pageable pageable);

    // find user inside company
    Optional<User> findByIdAndCompanyId(Long id, Long companyId);

    // email check inside company
    boolean existsByCompanyIdAndEmail(Long companyId, String email);

    long countByCompanyId(Long companyId);

    List<User> findByCompanyIdAndIsActive(Long companyId, boolean active);

    long countByCompanyIdAndRole(Long companyId, Role role);
}
