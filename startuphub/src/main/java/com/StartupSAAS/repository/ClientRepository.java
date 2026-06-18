package com.StartupSAAS.repository;

import com.StartupSAAS.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    // Single client with full details:
    // user + company + address full chain
    @Query("""
        SELECT c FROM Client c
        LEFT JOIN FETCH c.user
        LEFT JOIN FETCH c.company
        LEFT JOIN FETCH c.address a
        LEFT JOIN FETCH a.postOffice po
        LEFT JOIN FETCH po.policeStation ps
        LEFT JOIN FETCH ps.district d
        LEFT JOIN FETCH d.division dv
        LEFT JOIN FETCH dv.country
        WHERE c.id = :id
    """)
    Optional<Client> findByIdWithDetails(@Param("id") Long id);

    // All clients with full details
    @Query("""
        SELECT c FROM Client c
        LEFT JOIN FETCH c.user
        LEFT JOIN FETCH c.company
        LEFT JOIN FETCH c.address a
        LEFT JOIN FETCH a.postOffice po
        LEFT JOIN FETCH po.policeStation ps
        LEFT JOIN FETCH ps.district d
        LEFT JOIN FETCH d.division dv
        LEFT JOIN FETCH dv.country
    """)
    List<Client> findAllWithDetails();

    // All clients in a company
    @Query("""
        SELECT c FROM Client c
        LEFT JOIN FETCH c.user
        LEFT JOIN FETCH c.company
        LEFT JOIN FETCH c.address a
        LEFT JOIN FETCH a.postOffice po
        LEFT JOIN FETCH po.policeStation ps
        LEFT JOIN FETCH ps.district d
        LEFT JOIN FETCH d.division dv
        LEFT JOIN FETCH dv.country
        WHERE c.company.id = :companyId
    """)
    List<Client> findByCompanyId(@Param("companyId") Long companyId);

    // Active clients in a company
    List<Client> findByCompanyIdAndActiveTrue(Long companyId);

    boolean existsByUserId(Long userId);
}
