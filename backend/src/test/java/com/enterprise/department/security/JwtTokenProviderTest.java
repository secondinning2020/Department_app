package com.enterprise.department.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for JwtTokenProvider
 * Tests JWT token generation, validation, and extraction
 */
class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;
    private String secret;
    private Long expiration;
    private UserDetails testUserDetails;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider();

        // Use a test secret key (must be at least 256 bits for HS256)
        secret = "test-secret-key-for-jwt-token-generation-must-be-long-enough-for-hs256-algorithm";
        expiration = 3600000L; // 1 hour

        // Set private fields using reflection
        ReflectionTestUtils.setField(jwtTokenProvider, "secret", secret);
        ReflectionTestUtils.setField(jwtTokenProvider, "expiration", expiration);

        // Create test user details
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        testUserDetails = new User("testuser", "password", authorities);
    }

    @Test
    void generateToken_WithValidUserDetails_ShouldReturnToken() {
        // Act
        String token = jwtTokenProvider.generateToken(testUserDetails);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.split("\\.").length == 3); // JWT has 3 parts
    }

    @Test
    void extractUsername_WithValidToken_ShouldReturnUsername() {
        // Arrange
        String token = jwtTokenProvider.generateToken(testUserDetails);

        // Act
        String username = jwtTokenProvider.extractUsername(token);

        // Assert
        assertEquals("testuser", username);
    }

    @Test
    void extractUsername_WithDifferentUser_ShouldReturnCorrectUsername() {
        // Arrange
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_HR"));
        UserDetails userDetails = new User("johndoe", "password", authorities);
        String token = jwtTokenProvider.generateToken(userDetails);

        // Act
        String username = jwtTokenProvider.extractUsername(token);

        // Assert
        assertEquals("johndoe", username);
    }

    @Test
    void validateToken_WithValidToken_ShouldReturnTrue() {
        // Arrange
        String token = jwtTokenProvider.generateToken(testUserDetails);

        // Act
        boolean isValid = jwtTokenProvider.validateToken(token, testUserDetails);

        // Assert
        assertTrue(isValid);
    }

    @Test
    void validateToken_WithInvalidToken_ShouldReturnFalse() {
        // Arrange
        String invalidToken = "invalid.token.here";

        // Act
        boolean isValid = jwtTokenProvider.validateToken(invalidToken, testUserDetails);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void validateToken_WithExpiredToken_ShouldReturnFalse() {
        // Arrange - Create an expired token
        ReflectionTestUtils.setField(jwtTokenProvider, "expiration", -1000L);
        String expiredToken = jwtTokenProvider.generateToken(testUserDetails);

        // Reset to normal expiration
        ReflectionTestUtils.setField(jwtTokenProvider, "expiration", expiration);

        // Act
        boolean isValid = jwtTokenProvider.validateToken(expiredToken, testUserDetails);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void validateToken_WithNullToken_ShouldReturnFalse() {
        // Act
        boolean isValid = jwtTokenProvider.validateToken(null, testUserDetails);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void validateToken_WithEmptyToken_ShouldReturnFalse() {
        // Act
        boolean isValid = jwtTokenProvider.validateToken("", testUserDetails);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void validateToken_WithWrongUser_ShouldReturnFalse() {
        // Arrange
        String token = jwtTokenProvider.generateToken(testUserDetails);

        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        UserDetails differentUser = new User("differentuser", "password", authorities);

        // Act
        boolean isValid = jwtTokenProvider.validateToken(token, differentUser);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void generateToken_WithDifferentUsers_ShouldGenerateDifferentTokens() {
        // Arrange
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        UserDetails user1 = new User("user1", "password", authorities);
        UserDetails user2 = new User("user2", "password", authorities);

        // Act
        String token1 = jwtTokenProvider.generateToken(user1);
        String token2 = jwtTokenProvider.generateToken(user2);

        // Assert
        assertNotEquals(token1, token2);
        assertEquals("user1", jwtTokenProvider.extractUsername(token1));
        assertEquals("user2", jwtTokenProvider.extractUsername(token2));
    }

    @Test
    void extractExpiration_WithValidToken_ShouldReturnFutureDate() {
        // Arrange
        String token = jwtTokenProvider.generateToken(testUserDetails);

        // Act
        Date expirationDate = jwtTokenProvider.extractExpiration(token);

        // Assert
        assertNotNull(expirationDate);
        assertTrue(expirationDate.after(new Date()));
    }

    @Test
    void extractClaim_WithValidToken_ShouldExtractSubject() {
        // Arrange
        String token = jwtTokenProvider.generateToken(testUserDetails);

        // Act
        String subject = jwtTokenProvider.extractClaim(token, Claims::getSubject);

        // Assert
        assertEquals("testuser", subject);
    }

    @Test
    void generateToken_ShouldIncludeRoleInClaims() {
        // Arrange
        String token = jwtTokenProvider.generateToken(testUserDetails);

        // Act - Extract role from claims
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        String role = (String) claims.get("role");

        // Assert
        assertEquals("ROLE_ADMIN", role);
    }

    @Test
    void validateToken_WithTamperedToken_ShouldReturnFalse() {
        // Arrange
        String validToken = jwtTokenProvider.generateToken(testUserDetails);
        String tamperedToken = validToken.substring(0, validToken.length() - 5) + "XXXXX";

        // Act
        boolean isValid = jwtTokenProvider.validateToken(tamperedToken, testUserDetails);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void generateToken_ShouldSetIssuedAtDate() {
        // Arrange
        Date before = new Date();

        // Act
        String token = jwtTokenProvider.generateToken(testUserDetails);

        Date after = new Date();

        // Assert
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        Date issuedAt = claims.getIssuedAt();
        assertNotNull(issuedAt);
        assertTrue(issuedAt.after(before) || issuedAt.equals(before));
        assertTrue(issuedAt.before(after) || issuedAt.equals(after));
    }
}
