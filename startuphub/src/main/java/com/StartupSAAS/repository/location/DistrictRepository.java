package com.StartupSAAS.repository.location;

import com.StartupSAAS.entity.address.District;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DistrictRepository extends JpaRepository<District, Long> {

    // Single district with full chain: division → country
    @Query("""
        SELECT d FROM District d
        LEFT JOIN FETCH d.division dv
        LEFT JOIN FETCH dv.country
        WHERE d.id = :id
    """)
    Optional<District> findByIdWithDetails(@Param("id") Long id);

    // All districts with full chain
    @Query("""
        SELECT d FROM District d
        LEFT JOIN FETCH d.division dv
        LEFT JOIN FETCH dv.country
    """)
    List<District> findAllWithDetails();

    // Districts by division
    List<District> findByDivisionId(Long divisionId);
}
