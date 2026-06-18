package com.StartupSAAS.repository;

import com.StartupSAAS.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {

    @Query("""
        SELECT w FROM Wallet w
        LEFT JOIN FETCH w.client cl
        LEFT JOIN FETCH cl.user
        WHERE w.client.id = :clientId
    """)
    Optional<Wallet> findByClientId(@Param("clientId") Long clientId);
}
