package com.StartupSAAS.repository.location;

import com.StartupSAAS.entity.address.PostOffice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostOfficeRepository extends JpaRepository<PostOffice, Long> {

    // Single post office with full chain:
    // policeStation → district → division → country
    @Query("""
        SELECT po FROM PostOffice po
        LEFT JOIN FETCH po.policeStation ps
        LEFT JOIN FETCH ps.district d
        LEFT JOIN FETCH d.division dv
        LEFT JOIN FETCH dv.country
        WHERE po.id = :id
    """)
    Optional<PostOffice> findByIdWithDetails(@Param("id") Long id);

    // All post offices with full chain
    @Query("""
        SELECT po FROM PostOffice po
        LEFT JOIN FETCH po.policeStation ps
        LEFT JOIN FETCH ps.district d
        LEFT JOIN FETCH d.division dv
        LEFT JOIN FETCH dv.country
    """)
    List<PostOffice> findAllWithDetails();

    // Post offices by police station
    List<PostOffice> findByPoliceStationId(Long policeStationId);

    // Find by postal code
    Optional<PostOffice> findByPostalCode(String postalCode);
}
