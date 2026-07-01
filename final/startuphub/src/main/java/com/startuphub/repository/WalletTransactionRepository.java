package com.startuphub.repository;

import com.startuphub.entity.WalletTransaction;
import com.startuphub.enums.WalletTransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, Long> {

    Page<WalletTransaction> findByCompanyIdOrderByTransactedAtDesc(Long companyId, Pageable pageable);

    Page<WalletTransaction> findByCompanyIdAndTypeOrderByTransactedAtDesc(
        Long companyId, WalletTransactionType type, Pageable pageable);

    /**
     * Last transaction for a wallet — used to derive current running balance.
     */
    Optional<WalletTransaction> findTopByWalletIdOrderByTransactedAtDesc(Long walletId);

    @Query("SELECT SUM(t.amount) FROM WalletTransaction t WHERE t.company.id = :companyId AND t.type = :type AND t.transactedAt >= :from")
    Optional<BigDecimal> sumByCompanyIdAndTypeAfter(Long companyId, WalletTransactionType type, LocalDateTime from);
}
