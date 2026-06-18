package com.StartupSAAS.repository.location;

import com.StartupSAAS.entity.address.PoliceStation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PoliceStationRepository extends JpaRepository<PoliceStation, Long> {

    // Single police station with full chain: district → division → country
    @Query("""
        SELECT ps FROM PoliceStation ps
        LEFT JOIN FETCH ps.district d
        LEFT JOIN FETCH d.division dv
        LEFT JOIN FETCH dv.country
        WHERE ps.id = :id
    """)
    Optional<PoliceStation> findByIdWithDetails(@Param("id") Long id);

    // All police stations with full chain
    @Query("""
        SELECT ps FROM PoliceStation ps
        LEFT JOIN FETCH ps.district d
        LEFT JOIN FETCH d.division dv
        LEFT JOIN FETCH dv.country
    """)
    List<PoliceStation> findAllWithDetails();

    // Police stations by district
    List<PoliceStation> findByDistrictId(Long districtId);
}
