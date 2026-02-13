package com.moeware.ims.service;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.moeware.ims.dto.auth.AuthenticationRequest;
import com.moeware.ims.dto.auth.AuthenticationResponse;
import com.moeware.ims.dto.auth.RefreshTokenRequest;
import com.moeware.ims.dto.user.UserRegistrationDto;
import com.moeware.ims.dto.user.UserResponseDto;
import com.moeware.ims.exception.auth.InvalidCredentialsException;
import com.moeware.ims.exception.auth.InvalidTokenException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service for authentication operations
 *
 * @author MoeWare Team
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AuthenticationService {

    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    /**
     * Register a new user and return tokens
     */
    @Transactional
    public AuthenticationResponse register(UserRegistrationDto request) {
        log.info("Registering new user: {}", request.getUsername());

        // Register user
        UserResponseDto user = userService.registerUser(request);

        // Load full user details for token generation
        UserDetails userDetails = userService.loadUserByUsername(request.getUsername());

        // Generate tokens
        String accessToken = jwtService.generateToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        log.info("User registered and tokens generated: {}", user.getUsername());

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(24 * 60 * 60L) // 24 hours in seconds
                .user(user)
                .build();
    }

    /**
     * Authenticate user and return tokens
     */
    @Transactional
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        log.info("Authenticating user: {}", request.getUsername());

        try {
            // Authenticate using Spring Security
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()));
        } catch (BadCredentialsException e) {
            log.error("Authentication failed for user: {}", request.getUsername());
            throw new InvalidCredentialsException("Invalid username or password");
        }

        // Load user details
        UserDetails userDetails = userService.loadUserByUsername(request.getUsername());

        // Update last login
        userService.updateLastLogin(request.getUsername());

        // Generate tokens
        String accessToken = jwtService.generateToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        // Get user info
        UserResponseDto user = userService.getUserByUsername(request.getUsername());

        log.info("User authenticated successfully: {}", user.getUsername());

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(24 * 60 * 60L)
                .user(user)
                .build();
    }

    /**
     * Refresh access token using refresh token
     */
    public AuthenticationResponse refreshToken(RefreshTokenRequest request) {
        log.info("Refreshing access token");

        try {
            // Extract username from refresh token
            String username = jwtService.extractUsername(request.getRefreshToken());

            // Load user details
            UserDetails userDetails = userService.loadUserByUsername(username);

            // Validate refresh token
            if (!jwtService.isTokenValid(request.getRefreshToken(), userDetails)) {
                throw new InvalidTokenException("Invalid or expired refresh token");
            }

            // Generate new access token
            String accessToken = jwtService.generateToken(userDetails);

            // Get user info
            UserResponseDto user = userService.getUserByUsername(username);

            log.info("Access token refreshed for user: {}", username);

            return AuthenticationResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(request.getRefreshToken()) // Return same refresh token
                    .tokenType("Bearer")
                    .expiresIn(24 * 60 * 60L)
                    .user(user)
                    .build();

        } catch (InvalidTokenException e) {
            throw e;
        } catch (io.jsonwebtoken.JwtException e) {
            log.error("Token refresh failed: {}", e.getMessage());
            throw new InvalidTokenException("Failed to refresh token: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error during token refresh: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Validate token
     */
    public boolean validateToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return false;
        }

        try {
            String token = authHeader.substring(7);
            String username = jwtService.extractUsername(token);
            UserDetails userDetails = userService.loadUserByUsername(username);
            return jwtService.isTokenValid(token, userDetails);
        } catch (Exception e) {
            log.error("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Get current authenticated user
     */
    public UserResponseDto getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            throw new InvalidCredentialsException("No authenticated user found");
        }

        String username = authentication.getName();
        return userService.getUserByUsername(username);
    }
}