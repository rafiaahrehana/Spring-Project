package com.startuphub.repository;

import com.startuphub.entity.User;
import com.startuphub.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<User> findByIdAndDeletedFalse(Long id);

    Page<User> findByRole(Role role, Pageable pageable);

    // Data retention — find soft-deleted users past retention window
    @Query("SELECT u FROM User u WHERE u.deleted = true AND u.deletedAt < :cutoff")
    List<User> findDeletedBefore(LocalDateTime cutoff);
}
