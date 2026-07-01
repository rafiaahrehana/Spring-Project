package com.startuphub.repository;

import com.startuphub.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Optional<Employee> findByUserId(Long userId);

    Optional<Employee> findByIdAndCompanyId(Long id, Long companyId);

    boolean existsByUserIdAndCompanyId(Long userId, Long companyId);

    Page<Employee> findByCompanyId(Long companyId, Pageable pageable);

    Page<Employee> findByCompanyIdAndDepartmentId(Long companyId, Long departmentId, Pageable pageable);

    long countByCompanyId(Long companyId);

    /**
     * Resolves the company ID for a user who is an employee.
     * Used by AuthServiceImpl.resolveCompanyId() in Phase 3+.
     */
    @Query("SELECT e.company.id FROM Employee e WHERE e.user.id = :userId AND e.active = true AND e.deleted = false")
    Optional<Long> findCompanyIdByUserId(Long userId);
}
