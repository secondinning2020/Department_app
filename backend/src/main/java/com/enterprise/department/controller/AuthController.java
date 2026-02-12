package com.enterprise.department.controller;

import com.enterprise.department.dto.AuthRequest;
import com.enterprise.department.dto.AuthResponse;
import com.enterprise.department.entity.User;
import com.enterprise.department.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication Controller
 * Handles user authentication and registration
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "Authentication endpoints")
public class AuthController {

    private final AuthService authService;

    /**
     * Login endpoint
     * Public access
     */
    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user and return JWT token")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest authRequest) {
        log.info("Login request for user: {}", authRequest.getUsername());

        AuthResponse response = authService.login(authRequest);

        return ResponseEntity.ok(response);
    }

    /**
     * Register new user
     * Admin only
     */
    @PostMapping("/register")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Register new user", description = "Register new user (Admin only)")
    public ResponseEntity<String> register(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam User.Role role) {

        log.info("Registration request for user: {} with role: {}", username, role);

        authService.registerUser(username, password, role);

        return ResponseEntity.ok("User registered successfully");
    }
}
