package com.StartupSAAS.repository;

import com.StartupSAAS.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    @Query("""
        SELECT s FROM Subscription s
        LEFT JOIN FETCH s.company
        WHERE s.company.id = :companyId
    """)
    Optional<Subscription> findByCompanyId(@Param("companyId") Long companyId);
}
