package com.moeware.ims.controller;

import com.moeware.ims.dto.*;
import com.moeware.ims.dto.auth.AuthenticationRequest;
import com.moeware.ims.dto.auth.AuthenticationResponse;
import com.moeware.ims.dto.auth.RefreshTokenRequest;
import com.moeware.ims.dto.user.UserRegistrationDto;
import com.moeware.ims.dto.user.UserResponseDto;
import com.moeware.ims.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for authentication operations
 *
 * @author MoeWare Team
 * @version 1.0
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

        private final AuthenticationService authenticationService;

        /**
         * Register a new user
         * POST /api/auth/register
         */
        @PostMapping("/register")
        public ResponseEntity<ApiResponse<AuthenticationResponse>> register(
                        @Valid @RequestBody UserRegistrationDto request) {

                log.info("Registration request for username: {}", request.getUsername());

                AuthenticationResponse response = authenticationService.register(request);

                return ResponseEntity
                                .status(HttpStatus.CREATED)
                                .body(ApiResponse.success(response, "User registered successfully"));
        }

        /**
         * Authenticate user and generate tokens
         * POST /api/auth/login
         */
        @PostMapping("/login")
        public ResponseEntity<ApiResponse<AuthenticationResponse>> login(
                        @Valid @RequestBody AuthenticationRequest request) {

                log.info("Login request for username: {}", request.getUsername());

                AuthenticationResponse response = authenticationService.authenticate(request);

                return ResponseEntity.ok(
                                ApiResponse.success(response, "Login successful"));
        }

        /**
         * Refresh access token using refresh token
         * POST /api/auth/refresh
         */
        @PostMapping("/refresh")
        public ResponseEntity<ApiResponse<AuthenticationResponse>> refreshToken(
                        @Valid @RequestBody RefreshTokenRequest request) {

                log.info("Token refresh request");

                AuthenticationResponse response = authenticationService.refreshToken(request);

                return ResponseEntity.ok(
                                ApiResponse.success(response, "Token refreshed successfully"));
        }

        /**
         * Logout user (client-side token removal)
         * POST /api/auth/logout
         */
        @PostMapping("/logout")
        public ResponseEntity<ApiResponse<Void>> logout() {
                log.info("Logout request");

                // In a stateless JWT setup, logout is handled client-side by removing the token
                // Optionally, you can implement token blacklisting here

                return ResponseEntity.ok(
                                ApiResponse.success(null, "Logout successful"));
        }

        /**
         * Validate if a token is still valid
         * POST /api/auth/validate
         */
        @PostMapping("/validate")
        public ResponseEntity<ApiResponse<Boolean>> validateToken(
                        @RequestHeader("Authorization") String authHeader) {

                log.info("Token validation request");

                boolean isValid = authenticationService.validateToken(authHeader);

                return ResponseEntity.ok(
                                ApiResponse.success(isValid, "Token validation complete"));
        }

        /**
         * Get current authenticated user info
         * GET /api/auth/me
         */
        @GetMapping("/me")
        public ResponseEntity<ApiResponse<UserResponseDto>> getCurrentUser() {
                log.info("Get current user request");

                UserResponseDto user = authenticationService.getCurrentUser();

                return ResponseEntity.ok(
                                ApiResponse.success(user, "User information retrieved"));
        }
}