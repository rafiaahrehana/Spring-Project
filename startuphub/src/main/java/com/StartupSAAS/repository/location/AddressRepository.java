package com.StartupSAAS.repository.location;

import com.StartupSAAS.entity.address.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

    // Single address with full chain:
    // postOffice → policeStation → district → division → country
    @Query("""
        SELECT a FROM Address a
        LEFT JOIN FETCH a.postOffice po
        LEFT JOIN FETCH po.policeStation ps
        LEFT JOIN FETCH ps.district d
        LEFT JOIN FETCH d.division dv
        LEFT JOIN FETCH dv.country
        WHERE a.id = :id
    """)
    Optional<Address> findByIdWithDetails(@Param("id") Long id);
}
