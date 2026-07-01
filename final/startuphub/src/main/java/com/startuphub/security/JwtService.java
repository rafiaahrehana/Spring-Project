package com.startuphub.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JWT generation and validation.
 *
 * Token structure (access token):
 *   sub      — user email
 *   role     — user's Role enum value
 *   companyId — tenant ID (null for SUPER_ADMIN / SYSTEM_ADMIN)
 *   iat      — issued at
 *   exp      — expiry
 *
 * companyId is embedded at login time so every subsequent request
 * resolves the tenant in O(1) without a DB lookup.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtService {

    private final JwtProperties props;

    public String generateAccessToken(String email, String role, Long companyId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        if (companyId != null) {
            claims.put("companyId", companyId);
        }
        return buildToken(email, claims, props.getAccessExpirationMs());
    }

    public String generateRefreshToken(String email) {
        return buildToken(email, Map.of(), props.getRefreshExpirationMs());
    }

    private String buildToken(String subject, Map<String, Object> claims, long expiry) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
            .claims(claims)
            .subject(subject)
            .issuedAt(new Date(now))
            .expiration(new Date(now + expiry))
            .signWith(signingKey())
            .compact();
    }

    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts the tenant's company ID from the JWT.
     * Returns null for SUPER_ADMIN / SYSTEM_ADMIN tokens.
     */
    public Long extractCompanyId(String token) {
        try {
            Object value = extractClaim(token, claims -> claims.get("companyId"));
            if (value instanceof Integer i) return i.longValue();
            if (value instanceof Long l)    return l;
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public String extractRole(String token) {
        try {
            return (String) extractClaim(token, claims -> claims.get("role"));
        } catch (Exception e) {
            return null;
        }
    }

    public boolean isValid(String token) {
        try {
            Jwts.parser()
                .verifyWith(signingKey())
                .build()
                .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.debug("Invalid JWT: {}", e.getMessage());
            return false;
        }
    }

    public boolean isExpired(String token) {
        try {
            Date exp = extractClaim(token, Claims::getExpiration);
            return exp.before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    private <T> T extractClaim(String token, Function<Claims, T> resolver) {
        return resolver.apply(
            Jwts.parser()
                .verifyWith(signingKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
        );
    }

    private SecretKey signingKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(props.getSecret()));
    }
}
