package com.startuphub.entity;

import com.startuphub.enums.TokenType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Persistent store for all short-lived auth tokens.
 *
 * Covers three distinct flows:
 *   REFRESH           — 7-day JWT refresh, rotated on every use
 *   EMAIL_VERIFICATION — 24-hour link sent after registration
 *   PASSWORD_RESET    — 15-minute link for password recovery
 *
 * Does NOT extend BaseEntity — tokens are not soft-deleted.
 * They expire (expiresAt) and are marked used/revoked.
 *
 * Cleanup: a scheduled job should delete tokens older than 30 days
 * to prevent table bloat in production.
 */
@Entity
@Table(
    name = "tokens",
    indexes = {
        @Index(name = "idx_tokens_token", columnList = "token"),
        @Index(name = "idx_tokens_user_type", columnList = "user_id, type")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 500)
    private String token;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 25)
    private TokenType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private boolean used = false;

    @Column(nullable = false)
    private boolean revoked = false;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    public boolean isValid() {
        return !used && !revoked && !isExpired();
    }
}
