package org.smarterp.core.security;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.smarterp.core.entity.Role;
import org.smarterp.core.entity.User;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    private String secretKey = "dGVzdFNlY3JldEtleUZvckp3dFRva2VuR2VuZXJhdGlvbkFuZFZhbGlkYXRpb25NdXN0QmVMb25nRW5vdWdo";
    private long expirationTime = 3600000; // 1 hour
    private long refreshExpirationTime = 86400000; // 24 hours

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "jwtSecret", secretKey);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", expirationTime);
        ReflectionTestUtils.setField(jwtService, "refreshExpiration", refreshExpirationTime);
    }

    @Test
    void testGenerateToken() {
        User user = createUser("testuser", "test@example.com");
        
        String token = jwtService.generateToken(user);

        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.length() > 0);
    }

    @Test
    void testGenerateRefreshToken() {
        User user = createUser("testuser", "test@example.com");
        
        String refreshToken = jwtService.generateRefreshToken(user);

        assertNotNull(refreshToken);
        assertFalse(refreshToken.isEmpty());
    }

    @Test
    void testExtractUsername() {
        User user = createUser("john.doe", "john@example.com");
        
        String token = jwtService.generateToken(user);
        String username = jwtService.extractUsername(token);

        assertEquals("john.doe", username);
    }

    @Test
    void testExtractExpiration() {
        User user = createUser("testuser", "test@example.com");
        
        String token = jwtService.generateToken(user);
        Date expiration = jwtService.extractClaim(token, Claims::getExpiration);

        assertNotNull(expiration);
        assertTrue(expiration.after(new Date()));
        
        long expectedExpiration = System.currentTimeMillis() + expirationTime;
        long actualExpiration = expiration.getTime();
        assertTrue(Math.abs(expectedExpiration - actualExpiration) < 5000);
    }

    @Test
    void testIsTokenExpired() {
        User user = createUser("testuser", "test@example.com");
        
        String token = jwtService.generateToken(user);
        assertFalse(jwtService.isTokenExpired(token));
    }

    @Test
    void testIsTokenValid() {
        User user = createUser("validuser", "valid@example.com");
        
        String token = jwtService.generateToken(user);
        boolean isValid = jwtService.isTokenValid(token, user);

        assertTrue(isValid);
    }

    @Test
    void testIsTokenValidWithDifferentUser() {
        User user1 = createUser("user1", "user1@example.com");
        User user2 = createUser("user2", "user2@example.com");

        String token = jwtService.generateToken(user1);
        boolean isValid = jwtService.isTokenValid(token, user2);

        assertFalse(isValid);
    }

    @Test
    void testIsTokenValidWithInvalidToken() {
        User user = createUser("testuser", "test@example.com");
        
        String invalidToken = "invalid.token.here";
        assertThrows(Exception.class, () -> jwtService.isTokenValid(invalidToken, user));
    }

    @Test
    void testExtractClaim() {
        User user = createUser("testuser", "test@example.com");
        
        String token = jwtService.generateToken(user);
        String subject = jwtService.extractClaim(token, Claims::getSubject);

        assertEquals("testuser", subject);
    }

    @Test
    void testGetJwtExpiration() {
        long expiration = jwtService.getJwtExpiration();
        assertEquals(expirationTime, expiration);
    }

    @Test
    void testTokenContainsUserId() {
        User user = createUser("testuser", "test@example.com");
        
        String token = jwtService.generateToken(user);
        String userId = jwtService.extractClaim(token, claims -> claims.get("userId", String.class));

        assertNotNull(userId);
    }

    @Test
    void testTokenContainsRoles() {
        User user = createUser("adminuser", "admin@example.com");
        Set<Role> roles = new HashSet<>();
        Role adminRole = new Role();
        adminRole.setName("ADMIN");
        roles.add(adminRole);
        user.setRoles(roles);
        
        String token = jwtService.generateToken(user);
        
        @SuppressWarnings("unchecked")
        java.util.List<String> tokenRoles = jwtService.extractClaim(token, claims -> 
            (java.util.List<String>) claims.get("roles"));

        assertNotNull(tokenRoles);
        assertTrue(tokenRoles.contains("ADMIN"));
    }

    @Test
    void testRefreshTokenHasTypeClaim() {
        User user = createUser("testuser", "test@example.com");
        
        String refreshToken = jwtService.generateRefreshToken(user);
        String type = jwtService.extractClaim(refreshToken, claims -> claims.get("type", String.class));

        assertEquals("refresh", type);
    }

    @Test
    void testTokenExpiration() throws InterruptedException {
        JwtService shortLivedJwtService = new JwtService();
        ReflectionTestUtils.setField(shortLivedJwtService, "jwtSecret", secretKey);
        ReflectionTestUtils.setField(shortLivedJwtService, "jwtExpiration", 2000); // 2 seconds to allow for processing time
        ReflectionTestUtils.setField(shortLivedJwtService, "refreshExpiration", refreshExpirationTime);

        User user = createUser("testuser", "test@example.com");
        
        String token = shortLivedJwtService.generateToken(user);
        assertTrue(shortLivedJwtService.isTokenValid(token, user));
        
        Thread.sleep(2500); // Wait longer than expiration
        
        assertThrows(io.jsonwebtoken.JwtException.class, () -> shortLivedJwtService.isTokenValid(token, user));
    }

    private User createUser(String username, String email) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword("password");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setEnabled(true);
        user.setUserId(UUID.randomUUID());
        return user;
    }
}
