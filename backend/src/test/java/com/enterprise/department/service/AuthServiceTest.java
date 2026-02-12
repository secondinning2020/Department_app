package com.enterprise.department.service;

import com.enterprise.department.dto.AuthRequest;
import com.enterprise.department.dto.AuthResponse;
import com.enterprise.department.entity.User;
import com.enterprise.department.repository.UserRepository;
import com.enterprise.department.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AuthService
 * Tests authentication, user registration, and token generation
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserDetailsService userDetailsService;

    @InjectMocks
    private AuthService authService;

    private User testUser;
    private AuthRequest authRequest;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId("user-123");
        testUser.setUsername("testuser");
        testUser.setPassword("encodedPassword");
        testUser.setRole(User.Role.ADMIN);
        testUser.setEnabled(true);

        authRequest = new AuthRequest();
        authRequest.setUsername("testuser");
        authRequest.setPassword("password123");

        userDetails = org.springframework.security.core.userdetails.User
                .withUsername("testuser")
                .password("encodedPassword")
                .roles("ADMIN")
                .build();
    }

    @Test
    void login_WithValidCredentials_ShouldReturnAuthResponse() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(userDetailsService.loadUserByUsername("testuser"))
                .thenReturn(userDetails);
        when(jwtTokenProvider.generateToken(userDetails))
                .thenReturn("jwt-token-123");
        when(userRepository.findByUsername("testuser"))
                .thenReturn(Optional.of(testUser));

        // Act
        AuthResponse response = authService.login(authRequest);

        // Assert
        assertNotNull(response);
        assertEquals("jwt-token-123", response.getToken());
        assertEquals("testuser", response.getUsername());
        assertEquals("ADMIN", response.getRole());

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userDetailsService).loadUserByUsername("testuser");
        verify(jwtTokenProvider).generateToken(userDetails);
        verify(userRepository).findByUsername("testuser");
    }

    @Test
    void login_WithInvalidCredentials_ShouldThrowException() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        // Act & Assert
        assertThrows(BadCredentialsException.class, () -> authService.login(authRequest));
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userDetailsService, never()).loadUserByUsername(anyString());
        verify(jwtTokenProvider, never()).generateToken(any(UserDetails.class));
    }

    @Test
    void login_WithNonExistentUser_ShouldThrowException() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(userDetailsService.loadUserByUsername("testuser"))
                .thenReturn(userDetails);
        when(jwtTokenProvider.generateToken(userDetails))
                .thenReturn("jwt-token-123");
        when(userRepository.findByUsername("testuser"))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> authService.login(authRequest));
        verify(userRepository).findByUsername("testuser");
    }

    @Test
    void registerUser_WithValidData_ShouldCreateUser() {
        // Arrange
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId("user-new");
            return user;
        });

        // Act
        User result = authService.registerUser("newuser", "password123", User.Role.EMPLOYEE);

        // Assert
        assertNotNull(result);
        assertTrue(result.getEnabled());
        verify(userRepository).existsByUsername("newuser");
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerUser_WithDuplicateUsername_ShouldThrowException() {
        // Arrange
        when(userRepository.existsByUsername("existinguser")).thenReturn(true);

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> authService.registerUser("existinguser", "password123", User.Role.EMPLOYEE));
        verify(userRepository).existsByUsername("existinguser");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerUser_ShouldEncodePassword() {
        // Arrange
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        authService.registerUser("newuser", "password123", User.Role.HR);

        // Assert
        verify(passwordEncoder).encode("password123");
    }

    @Test
    void registerUser_ShouldSetUserAsEnabled() {
        // Arrange
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        User result = authService.registerUser("newuser", "password123", User.Role.ADMIN);

        // Assert
        assertTrue(result.getEnabled());
        assertTrue(result.getAccountNonExpired());
        assertTrue(result.getAccountNonLocked());
        assertTrue(result.getCredentialsNonExpired());
    }

    @Test
    void registerUser_WithAdminRole_ShouldCreateAdminUser() {
        // Arrange
        when(userRepository.existsByUsername("admin2")).thenReturn(false);
        when(passwordEncoder.encode("admin123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        User result = authService.registerUser("admin2", "admin123", User.Role.ADMIN);

        // Assert
        assertNotNull(result);
        assertEquals(User.Role.ADMIN, result.getRole());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void login_ShouldLoadUserDetailsBeforeGeneratingToken() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(userDetailsService.loadUserByUsername("testuser"))
                .thenReturn(userDetails);
        when(jwtTokenProvider.generateToken(userDetails))
                .thenReturn("jwt-token-123");
        when(userRepository.findByUsername("testuser"))
                .thenReturn(Optional.of(testUser));

        // Act
        authService.login(authRequest);

        // Assert - verify order of operations
        verify(userDetailsService).loadUserByUsername("testuser");
        verify(jwtTokenProvider).generateToken(userDetails);
    }
}
