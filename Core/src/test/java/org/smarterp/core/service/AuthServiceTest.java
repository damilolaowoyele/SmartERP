package org.smarterp.core.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.smarterp.core.dto.AuthenticationRequest;
import org.smarterp.core.dto.AuthenticationResponse;
import org.smarterp.core.dto.RegisterRequest;
import org.smarterp.core.dto.UserDTO;
import org.smarterp.core.entity.Permission;
import org.smarterp.core.entity.Role;
import org.smarterp.core.entity.User;
import org.smarterp.core.repository.PermissionRepository;
import org.smarterp.core.repository.RoleRepository;
import org.smarterp.core.repository.UserRepository;
import org.smarterp.core.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PermissionRepository permissionRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private User testUser;
    private Role testRole;
    private Permission testPermission;

    @BeforeEach
    void setUp() {
        testPermission = new Permission();
        testPermission.setName("READ_PRODUCTS");
        testPermission.setDescription("Can read products");

        testRole = new Role();
        testRole.setName("USER");
        testRole.setDescription("Standard user role");
        testRole.setPermissions(Set.of(testPermission));

        testUser = new User();
        testUser.setUserId(UUID.randomUUID());
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setEnabled(true);
        testUser.setRoles(Set.of(testRole));
    }

    @Test
    void testRegisterUser_Success() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newuser");
        request.setPassword("password123");
        request.setEmail("new@example.com");
        request.setFirstName("New");
        request.setLastName("User");

        when(userRepository.existsByUsername(request.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
        
        Role defaultRole = new Role();
        defaultRole.setName("USER");
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(defaultRole));
        
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtService.generateToken(any(User.class))).thenReturn("test.jwt.token");
        when(jwtService.generateRefreshToken(any(User.class))).thenReturn("test.refresh.token");
        when(jwtService.getJwtExpiration()).thenReturn(3600000L);

        AuthenticationResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals("test.jwt.token", response.getAccessToken());
        verify(userRepository).save(any(User.class));
        verify(passwordEncoder).encode(request.getPassword());
    }

    @Test
    void testRegisterUser_UsernameAlreadyExists() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("existinguser");
        request.setPassword("password123");
        request.setEmail("test@example.com");

        when(userRepository.existsByUsername(request.getUsername())).thenReturn(true);

        assertThrows(RuntimeException.class, () -> authService.register(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void testRegisterUser_EmailAlreadyExists() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newuser");
        request.setPassword("password123");
        request.setEmail("existing@example.com");

        when(userRepository.existsByUsername(request.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        assertThrows(RuntimeException.class, () -> authService.register(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void testAuthenticate_Success() {
        AuthenticationRequest request = new AuthenticationRequest("testuser", "password123");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken("testuser", "password123"));
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(jwtService.generateToken(any(User.class))).thenReturn("test.jwt.token");
        when(jwtService.generateRefreshToken(any(User.class))).thenReturn("test.refresh.token");
        when(jwtService.getJwtExpiration()).thenReturn(3600000L);

        AuthenticationResponse response = authService.authenticate(request);

        assertNotNull(response);
        assertEquals("test.jwt.token", response.getAccessToken());
        assertEquals("testuser", response.getUser().getUsername());
        verify(authenticationManager).authenticate(any());
        verify(jwtService).generateToken(any());
    }

    @Test
    void testAuthenticate_BadCredentials() {
        AuthenticationRequest request = new AuthenticationRequest("testuser", "wrongpassword");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        assertThrows(BadCredentialsException.class, () -> authService.authenticate(request));
    }

    @Test
    void testRefreshToken_ValidToken() {
        String refreshToken = "valid.refresh.token";
        
        when(jwtService.extractUsername(refreshToken)).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(jwtService.isTokenValid(refreshToken, testUser)).thenReturn(true);
        when(jwtService.generateToken(any(User.class))).thenReturn("new.jwt.token");
        when(jwtService.generateRefreshToken(any(User.class))).thenReturn("new.refresh.token");
        when(jwtService.getJwtExpiration()).thenReturn(3600000L);

        AuthenticationResponse response = authService.refreshToken(refreshToken);

        assertNotNull(response);
        assertEquals("new.jwt.token", response.getAccessToken());
        verify(jwtService).extractUsername(refreshToken);
        verify(jwtService).generateToken(any());
    }

    @Test
    void testRefreshToken_UserNotFound() {
        String refreshToken = "invalid.refresh.token";
        
        when(jwtService.extractUsername(refreshToken)).thenReturn("nonexistent");
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> authService.refreshToken(refreshToken));
    }

    @Test
    void testRefreshToken_InvalidToken() {
        String refreshToken = "expired.token";
        
        when(jwtService.extractUsername(refreshToken)).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(jwtService.isTokenValid(refreshToken, testUser)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> authService.refreshToken(refreshToken));
    }

    @Test
    void testGetCurrentUser() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        UserDTO profile = authService.getCurrentUser("testuser");

        assertNotNull(profile);
        assertEquals("testuser", profile.getUsername());
        assertEquals("test@example.com", profile.getEmail());
        assertEquals("Test", profile.getFirstName());
        assertEquals("User", profile.getLastName());
    }

    @Test
    void testGetCurrentUser_UserNotFound() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> authService.getCurrentUser("nonexistent"));
    }
}
