package com.enterprise.department.service;

import com.enterprise.department.dto.AuthRequest;
import com.enterprise.department.dto.AuthResponse;
import com.enterprise.department.entity.User;
import com.enterprise.department.repository.UserRepository;
import com.enterprise.department.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Authentication Service
 * Handles user authentication and registration
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Authenticate user and generate JWT token
     *
     * @param authRequest Login credentials
     * @return Authentication response with JWT token
     */
    @Transactional(readOnly = true)
    public AuthResponse login(AuthRequest authRequest) {
        log.info("Authenticating user: {}", authRequest.getUsername());

        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequest.getUsername(),
                        authRequest.getPassword()));

        // Load user details
        UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());

        // Generate JWT token
        String token = jwtTokenProvider.generateToken(userDetails);

        // Get user role
        User user = userRepository.findByUsername(authRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        log.info("User authenticated successfully: {}", authRequest.getUsername());

        return new AuthResponse(token, user.getUsername(), user.getRole().name());
    }

    /**
     * Register new user (Admin only)
     *
     * @param username Username
     * @param password Password
     * @param role     User role
     * @return Created user
     */
    @Transactional
    public User registerUser(String username, String password, User.Role role) {
        log.info("Registering new user: {} with role: {}", username, role);

        // Check if username already exists
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already exists");
        }

        // Create new user
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        user.setEnabled(true);
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);

        User savedUser = userRepository.save(user);

        log.info("User registered successfully: {}", username);

        return savedUser;
    }
}
