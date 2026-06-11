package com.saas.luminex.security;

import com.saas.luminex.entity.User;
import com.saas.luminex.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.*;

@DisplayName("JwtUtil Unit Tests")
class JwtUtilTest {

    private JwtUtil jwtUtil;

    private static final String TEST_SECRET =
            "LumiNexSuperSecretKeyMustBe256BitsLongForHMACSHA256AlgorithmTest";

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "jwtSecret", TEST_SECRET);
        ReflectionTestUtils.setField(jwtUtil, "jwtExpirationMs", 86400000L);       // 1 day
        ReflectionTestUtils.setField(jwtUtil, "refreshExpirationMs", 604800000L);  // 7 days
    }

    private User buildUser(Role role) {
        return User.builder()
                .id(1L)
                .name("Test User")
                .email("test@luminex.com")
                .password("encoded")
                .role(role)
                .isActive(true)
                .build();
    }

    @Test
    @DisplayName("generateToken() - produces a non-blank token")
    void generateToken_producesToken() {
        String token = jwtUtil.generateToken(buildUser(Role.CLIENT));
        assertThat(token).isNotBlank();
    }

    @Test
    @DisplayName("generateToken() - token contains correct email as subject")
    void generateToken_containsEmail() {
        String token = jwtUtil.generateToken(buildUser(Role.ADMIN));
        assertThat(jwtUtil.extractEmail(token)).isEqualTo("test@luminex.com");
    }

    @Test
    @DisplayName("validateToken() - fresh token is valid")
    void validateToken_freshToken_isValid() {
        String token = jwtUtil.generateToken(buildUser(Role.SUPER_ADMIN));
        assertThat(jwtUtil.validateToken(token)).isTrue();
    }

    @Test
    @DisplayName("validateToken() - tampered token is invalid")
    void validateToken_tamperedToken_isFalse() {
        String token = jwtUtil.generateToken(buildUser(Role.CLIENT));
        String tampered = token.substring(0, token.length() - 5) + "XXXXX";
        assertThat(jwtUtil.validateToken(tampered)).isFalse();
    }

    @Test
    @DisplayName("validateToken() - completely random string is invalid")
    void validateToken_randomString_isFalse() {
        assertThat(jwtUtil.validateToken("not.a.jwt.at.all")).isFalse();
    }

    @Test
    @DisplayName("generateRefreshToken() - is different from access token")
    void refreshToken_isDifferentFromAccessToken() {
        User user = buildUser(Role.CLIENT);
        String access = jwtUtil.generateToken(user);
        String refresh = jwtUtil.generateRefreshToken(user);
        assertThat(access).isNotEqualTo(refresh);
    }

    @Test
    @DisplayName("tokens for different roles - both contain same email")
    void tokens_differentRoles_sameEmail() {
        for (Role role : Role.values()) {
            String token = jwtUtil.generateToken(buildUser(role));
            assertThat(jwtUtil.extractEmail(token)).isEqualTo("test@luminex.com");
        }
    }
}
