package com.StartupSAAS.repository;

import com.StartupSAAS.entity.User;
import com.StartupSAAS.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    // Single user with company
    @Query("""
        SELECT u FROM User u
        LEFT JOIN FETCH u.company
        WHERE u.id = :id
    """)
    Optional<User> findByIdWithDetails(@Param("id") Long id);

    // All users in a company
    @Query("""
        SELECT u FROM User u
        LEFT JOIN FETCH u.company
        WHERE u.company.id = :companyId
    """)
    List<User> findByCompanyId(@Param("companyId") Long companyId);

    // Users by role in a company
    List<User> findByCompanyIdAndRole(Long companyId, Role role);

    // Active users in a company
    List<User> findByCompanyIdAndIsActiveTrue(Long companyId);
}
