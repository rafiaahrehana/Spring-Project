package com.StartupSAAS.repository.location;

import com.StartupSAAS.entity.address.Division;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DivisionRepository extends JpaRepository<Division, Long> {

    // All divisions with their country
    @Query("""
        SELECT d FROM Division d
        LEFT JOIN FETCH d.country
    """)
    List<Division> findAllWithDetails();

    // Single division with country
    @Query("""
        SELECT d FROM Division d
        LEFT JOIN FETCH d.country
        WHERE d.id = :id
    """)
    Optional<Division> findByIdWithDetails(@Param("id") Long id);

    // Divisions by country
    List<Division> findByCountryId(Long countryId);
}
