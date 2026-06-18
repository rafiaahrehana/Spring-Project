package com.StartupSAAS.repository;

import com.StartupSAAS.entity.Company;
import com.StartupSAAS.enums.CompanyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {

    // Single company with owner user
    @Query("""
        SELECT c FROM Company c
        LEFT JOIN FETCH c.owner
        WHERE c.id = :id
    """)
    Optional<Company> findByIdWithDetails(@Param("id") Long id);

    // All companies with owner — for SUPER_ADMIN
    @Query("""
        SELECT c FROM Company c
        LEFT JOIN FETCH c.owner
    """)
    List<Company> findAllWithDetails();

    Optional<Company> findBySubdomain(String subdomain);

    boolean existsBySubdomain(String subdomain);

    boolean existsByName(String name);

    // Companies by status — for Super Admin monitoring
    List<Company> findByStatus(CompanyStatus status);
}
