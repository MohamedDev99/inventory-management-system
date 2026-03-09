package com.moeware.ims.service;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.moeware.ims.dto.auth.AuthenticationRequest;
import com.moeware.ims.dto.auth.AuthenticationResponse;
import com.moeware.ims.dto.auth.TokenPair;
import com.moeware.ims.dto.user.UserRegistrationDto;
import com.moeware.ims.dto.user.UserResponseDto;
import com.moeware.ims.entity.User;
import com.moeware.ims.exception.ResourceNotFoundException;
import com.moeware.ims.exception.auth.AccountDisabledException;
import com.moeware.ims.exception.auth.AccountLockedException;
import com.moeware.ims.exception.auth.InvalidCredentialsException;
import com.moeware.ims.exception.auth.InvalidTokenException;
import com.moeware.ims.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service for authentication operations.
 *
 * <p>
 * Token delivery: JWT tokens are <b>never</b> returned inside this service's
 * response objects.
 * Instead, the service returns a {@link TokenPair} alongside the
 * {@link AuthenticationResponse}
 * so the controller can set them as HttpOnly cookies on the HTTP response.
 * </p>
 *
 * <p>
 * Handles: registration, login (with account lockout), token refresh,
 * token validation, logout (Redis blacklist), and admin password reset.
 * </p>
 *
 * @author MoeWare Team
 * @version 1.2
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AuthenticationService {

    private final UserService userService;
    private final JwtService jwtService;
    private final TokenBlacklistService tokenBlacklistService;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;

    // ==========================================
    // Registration
    // ==========================================

    /**
     * Register a new user and generate JWT tokens.
     *
     * @return {@link AuthResult} containing the JSON response body + the token pair
     *         for cookie setting
     */
    @Transactional
    public AuthResult register(UserRegistrationDto request) {
        log.info("Registering new user: {}", request.getUsername());

        UserResponseDto user = userService.registerUser(request);
        UserDetails userDetails = userService.loadUserByUsername(request.getUsername());

        String accessToken = jwtService.generateToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        log.info("User registered successfully: {}", user.getUsername());
        return new AuthResult(buildResponse(user), new TokenPair(accessToken, refreshToken));
    }

    // ==========================================
    // Login with lockout
    // ==========================================

    /**
     * Authenticate a user and generate JWT tokens.
     *
     * <p>
     * On successful login: resets failed-attempt counter and updates last-login
     * timestamp.<br>
     * On failed login: increments counter and locks the account after
     * {@link User#MAX_FAILED_ATTEMPTS} consecutive failures.
     * </p>
     *
     * @return {@link AuthResult} containing the JSON response body + the token pair
     *         for cookie setting
     * @throws AccountLockedException      if the account is currently locked
     * @throws AccountDisabledException    if the account is deactivated
     * @throws InvalidCredentialsException if credentials are wrong
     */
    @Transactional
    public AuthResult authenticate(AuthenticationRequest request) {
        log.info("Authentication attempt for username: {}", request.getUsername());

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid username or password"));

        if (!user.getIsActive()) {
            throw new AccountDisabledException();
        }

        if (user.isCurrentlyLocked()) {
            log.warn("Login attempt on locked account: {}", request.getUsername());
            throw new AccountLockedException(user.getLockedUntil());
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()));
        } catch (BadCredentialsException | UsernameNotFoundException e) {
            user.incrementFailedLoginAttempts();
            userRepository.save(user);

            if (user.isCurrentlyLocked()) {
                log.warn("Account locked after {} failed attempts: {}",
                        User.MAX_FAILED_ATTEMPTS, request.getUsername());
                throw new AccountLockedException(user.getLockedUntil());
            }

            int remaining = User.MAX_FAILED_ATTEMPTS - user.getFailedLoginAttempts();
            log.warn("Failed login for '{}'. {} attempt(s) remaining.", request.getUsername(), remaining);
            throw new InvalidCredentialsException(
                    "Invalid username or password. " + remaining + " attempt(s) remaining.");
        } catch (LockedException e) {
            throw new AccountLockedException(user.getLockedUntil());
        } catch (DisabledException e) {
            throw new AccountDisabledException();
        }

        // Successful login
        user.resetFailedLoginAttempts();
        user.updateLastLogin();
        userRepository.save(user);

        UserDetails userDetails = userService.loadUserByUsername(request.getUsername());
        String accessToken = jwtService.generateToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);
        UserResponseDto userDto = userService.getUserByUsername(request.getUsername());

        log.info("User authenticated successfully: {}", userDto.getUsername());
        return new AuthResult(buildResponse(userDto), new TokenPair(accessToken, refreshToken));
    }

    // ==========================================
    // Token Refresh
    // ==========================================

    /**
     * Issue a new access token using a valid refresh token.
     * The same refresh token is re-issued unchanged.
     *
     * @param rawRefreshToken the raw refresh token string (extracted from cookie by
     *                        controller)
     * @return {@link AuthResult} with a new access token (same refresh token)
     * @throws InvalidTokenException if the refresh token is invalid or expired
     */
    public AuthResult refreshToken(String rawRefreshToken) {
        log.info("Token refresh request");

        try {
            String username = jwtService.extractUsername(rawRefreshToken);
            UserDetails userDetails = userService.loadUserByUsername(username);

            if (!jwtService.isTokenValid(rawRefreshToken, userDetails)) {
                throw new InvalidTokenException("Invalid or expired refresh token");
            }

            String newAccessToken = jwtService.generateToken(userDetails);
            UserResponseDto user = userService.getUserByUsername(username);

            log.info("Access token refreshed for user: {}", username);
            return new AuthResult(buildResponse(user), new TokenPair(newAccessToken, rawRefreshToken));

        } catch (InvalidTokenException e) {
            throw e;
        } catch (io.jsonwebtoken.JwtException e) {
            log.error("Token refresh failed: {}", e.getMessage());
            throw new InvalidTokenException("Failed to refresh token: " + e.getMessage());
        }
    }

    // ==========================================
    // Logout
    // ==========================================

    /**
     * Blacklist the access token so it will be rejected on all subsequent requests.
     * The controller is responsible for also clearing the cookies.
     *
     * @param accessToken the raw access token string (extracted from cookie by
     *                    controller)
     */
    public void logout(String accessToken) {
        if (accessToken != null && !accessToken.isBlank()) {
            tokenBlacklistService.blacklistToken(accessToken);
            log.info("Access token blacklisted on logout");
        }
    }

    // ==========================================
    // Token Validation
    // ==========================================

    /**
     * Validate a token from either cookie or Authorization header.
     *
     * @param rawToken the raw JWT string (already extracted by the caller — no
     *                 "Bearer " prefix)
     * @return {@code true} if valid, not expired, and not blacklisted
     */
    public boolean validateToken(String rawToken) {
        if (rawToken == null || rawToken.isBlank()) {
            return false;
        }
        try {
            if (tokenBlacklistService.isBlacklisted(rawToken)) {
                return false;
            }
            String username = jwtService.extractUsername(rawToken);
            UserDetails userDetails = userService.loadUserByUsername(username);
            return jwtService.isTokenValid(rawToken, userDetails);
        } catch (Exception e) {
            log.error("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    // ==========================================
    // Current User
    // ==========================================

    /**
     * Return the currently authenticated user's profile.
     *
     * @throws InvalidCredentialsException if no authenticated user is found in the
     *                                     security context
     */
    public UserResponseDto getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            throw new InvalidCredentialsException("No authenticated user found");
        }

        return userService.getUserByUsername(authentication.getName());
    }

    // ==========================================
    // Admin Password Reset
    // ==========================================

    /**
     * Admin-only: forcefully reset any user's password.
     *
     * @param userId      the ID of the target user
     * @param newPassword plain-text password (BCrypt-encoded inside UserService)
     * @throws ResourceNotFoundException if the user does not exist
     */
    @Transactional
    public void adminResetPassword(Long userId, String newPassword) {
        log.info("Admin password reset for user id: {}", userId);
        userService.changePassword(userId, newPassword);
        log.info("Password reset completed for user id: {}", userId);
    }

    // ==========================================
    // Private Helpers
    // ==========================================

    private AuthenticationResponse buildResponse(UserResponseDto user) {
        return AuthenticationResponse.builder()
                .tokenType("Bearer")
                .expiresIn(24 * 60 * 60L)
                .user(user)
                .build();
    }

    // ==========================================
    // Inner Record: AuthResult
    // ==========================================

    /**
     * Combines the HTTP response body with the token pair.
     * The controller uses {@code tokens} to set HttpOnly cookies,
     * then returns {@code response} as the JSON body.
     */
    public record AuthResult(AuthenticationResponse response, TokenPair tokens) {
    }
}