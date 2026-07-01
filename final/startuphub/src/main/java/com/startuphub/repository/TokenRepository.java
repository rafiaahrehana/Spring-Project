package com.startuphub.repository;

import com.startuphub.entity.Token;
import com.startuphub.enums.TokenType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {

    Optional<Token> findByToken(String token);

    Optional<Token> findByTokenAndType(String token, TokenType type);

    @Transactional
    @Modifying
    @Query("DELETE FROM Token t WHERE t.user.id = :userId AND t.type = :type")
    void deleteByUserIdAndType(Long userId, TokenType type);

    @Transactional
    @Modifying
    @Query("UPDATE Token t SET t.revoked = true WHERE t.user.id = :userId AND t.type = :type AND t.revoked = false")
    void revokeAllByUserIdAndType(Long userId, TokenType type);

    // Cleanup — remove expired tokens past grace period
    @Transactional
    @Modifying
    @Query("DELETE FROM Token t WHERE t.expiresAt < :cutoff")
    void deleteExpiredBefore(LocalDateTime cutoff);
}
