package com.StartupSAAS.repository;

import com.StartupSAAS.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Optional<Wallet> findByCompanyId(Long companyId);
}