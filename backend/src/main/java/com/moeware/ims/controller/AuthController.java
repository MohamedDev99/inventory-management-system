package com.moeware.ims.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moeware.ims.dto.ApiResponseWpp;
import com.moeware.ims.dto.auth.AdminPasswordResetDto;
import com.moeware.ims.dto.auth.AuthenticationRequest;
import com.moeware.ims.dto.auth.AuthenticationResponse;
import com.moeware.ims.dto.user.UserRegistrationDto;
import com.moeware.ims.dto.user.UserResponseDto;
import com.moeware.ims.exception.auth.InvalidTokenException;
import com.moeware.ims.exception.handler.AuthExceptionHandler;
import com.moeware.ims.exception.handler.GlobalExceptionHandler;
import com.moeware.ims.service.AuthenticationService;
import com.moeware.ims.service.AuthenticationService.AuthResult;
import com.moeware.ims.service.CookieService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST controller for authentication operations.
 *
 * <p>
 * <b>Cookie strategy:</b> JWT tokens are delivered as HttpOnly cookies,
 * never in the JSON response body. This protects tokens from XSS attacks.
 * The cookies are:
 * </p>
 * <ul>
 * <li>{@code access_token} — short-lived, used on every authenticated
 * request</li>
 * <li>{@code refresh_token} — long-lived, used only at POST
 * /api/auth/refresh</li>
 * </ul>
 *
 * <p>
 * <b>Postman / API client support:</b> The
 * {@link com.moeware.ims.security.JwtAuthenticationFilter}
 * also accepts a standard {@code Authorization: Bearer <token>} header as a
 * fallback,
 * so non-browser clients continue to work normally.
 * </p>
 *
 * @author MoeWare Team
 * @version 1.2
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "Authentication APIs — registration, login, token refresh, logout, and admin password reset. "
                + "JWT tokens are set as HttpOnly cookies on the response.")
public class AuthController {

        private final AuthenticationService authenticationService;
        private final CookieService cookieService;

        // ==========================================
        // Register
        // ==========================================

        @Operation(summary = "Register a new user", description = "Create a new user account. On success, JWT tokens are set as HttpOnly cookies "
                        + "({@code access_token} and {@code refresh_token}). The JSON body contains user info only.")
        @ApiResponses({
                        @ApiResponse(responseCode = "201", description = "User registered — tokens set in cookies", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthenticationResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Validation error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = GlobalExceptionHandler.ValidationErrorResponse.class))),
                        @ApiResponse(responseCode = "409", description = "Username or email already exists", content = @Content(mediaType = "application/json", schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class)))
        })
        @PostMapping("/register")
        public ResponseEntity<ApiResponseWpp<AuthenticationResponse>> register(
                        @Valid @RequestBody UserRegistrationDto request,
                        HttpServletResponse response) {

                log.info("Registration request for username: {}", request.getUsername());

                AuthResult result = authenticationService.register(request);
                cookieService.addAccessTokenCookie(response, result.tokens().accessToken());
                cookieService.addRefreshTokenCookie(response, result.tokens().refreshToken());

                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(ApiResponseWpp.success(result.response(), "User registered successfully"));
        }

        // ==========================================
        // Login
        // ==========================================

        @Operation(summary = "Login", description = "Authenticate with username and password. On success, JWT tokens are set as HttpOnly cookies. "
                        + "Account locks for 30 minutes after 5 consecutive failed attempts.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Login successful — tokens set in cookies", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthenticationResponse.class))),
                        @ApiResponse(responseCode = "401", description = "Invalid credentials", content = @Content(mediaType = "application/json", schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class))),
                        @ApiResponse(responseCode = "423", description = "Account locked due to too many failed attempts", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthExceptionHandler.AccountLockedErrorResponse.class))) })

        @PostMapping("/login")
        public ResponseEntity<ApiResponseWpp<AuthenticationResponse>> login(
                        @Valid @RequestBody AuthenticationRequest request,
                        HttpServletResponse response) {

                log.info("Login request for username: {}", request.getUsername());

                AuthResult result = authenticationService.authenticate(request);
                cookieService.addAccessTokenCookie(response, result.tokens().accessToken());
                cookieService.addRefreshTokenCookie(response, result.tokens().refreshToken());

                return ResponseEntity.ok(ApiResponseWpp.success(result.response(), "Login successful"));
        }

        // ==========================================
        // Token Refresh (reads refresh token from cookie)
        // ==========================================

        @Operation(summary = "Refresh access token", description = "Reads the {@code refresh_token} cookie and issues a new access token. "
                        + "No request body needed for browser clients. "
                        + "The new access token is set in the {@code access_token} cookie.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Access token refreshed — new cookie set", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthenticationResponse.class))),
                        @ApiResponse(responseCode = "401", description = "Invalid or expired refresh token", content = @Content(mediaType = "application/json", schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class)))
        })
        @PostMapping("/refresh")
        public ResponseEntity<ApiResponseWpp<AuthenticationResponse>> refreshToken(
                        HttpServletRequest request,
                        HttpServletResponse response) {

                log.info("Token refresh request");

                // Extract refresh token from cookie
                String refreshToken = cookieService.extractRefreshToken(request);
                if (refreshToken == null || refreshToken.isBlank()) {
                        throw new InvalidTokenException("Refresh token cookie is missing");
                }

                AuthResult result = authenticationService.refreshToken(refreshToken);
                // Only refresh the access token cookie; keep the same refresh token cookie
                cookieService.addAccessTokenCookie(response, result.tokens().accessToken());

                return ResponseEntity.ok(ApiResponseWpp.success(result.response(), "Token refreshed successfully"));
        }

        // ==========================================
        // Logout
        // ==========================================

        @Operation(summary = "Logout", description = "Blacklists the current access token in Redis and clears both JWT cookies. "
                        + "Any subsequent request using the old token will be rejected.", security = @SecurityRequirement(name = "bearerAuth"))
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Logout successful — cookies cleared"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized")
        })
        @PostMapping("/logout")
        public ResponseEntity<ApiResponseWpp<Void>> logout(
                        HttpServletRequest request,
                        HttpServletResponse response) {

                log.info("Logout request");

                // Blacklist the access token (extracted from cookie or header)
                String accessToken = cookieService.extractAccessToken(request);
                if (accessToken == null || accessToken.isBlank()) {
                        // Fallback: try Authorization header (for API clients)
                        String authHeader = request.getHeader("Authorization");
                        if (authHeader != null && authHeader.startsWith("Bearer ")) {
                                accessToken = authHeader.substring(7);
                        }
                }

                authenticationService.logout(accessToken);

                // Always clear cookies regardless of token source
                cookieService.clearAuthCookies(response);

                return ResponseEntity.ok(ApiResponseWpp.success(null, "Logout successful"));
        }

        // ==========================================
        // Token Validation
        // ==========================================

        @Operation(summary = "Validate token", description = "Check if the current access token (cookie or Authorization header) is valid, "
                        + "not expired, and not blacklisted.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Validation result returned")
        })
        @PostMapping("/validate")
        public ResponseEntity<ApiResponseWpp<Boolean>> validateToken(HttpServletRequest request) {

                log.info("Token validation request");

                // Try cookie first, then Authorization header
                String token = cookieService.extractAccessToken(request);
                if (token == null || token.isBlank()) {
                        String authHeader = request.getHeader("Authorization");
                        if (authHeader != null && authHeader.startsWith("Bearer ")) {
                                token = authHeader.substring(7);
                        }
                }

                boolean isValid = authenticationService.validateToken(token);
                return ResponseEntity.ok(ApiResponseWpp.success(isValid, "Token validation complete"));
        }

        // ==========================================
        // Current User
        // ==========================================

        @Operation(summary = "Get current authenticated user", description = "Retrieve the profile of the currently authenticated user.", security = @SecurityRequirement(name = "bearerAuth"))
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "User information retrieved", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDto.class))),
                        @ApiResponse(responseCode = "401", description = "Unauthorized — no valid session cookie or token")
        })
        @GetMapping("/me")
        public ResponseEntity<ApiResponseWpp<UserResponseDto>> getCurrentUser() {
                log.info("Get current user request");
                UserResponseDto user = authenticationService.getCurrentUser();
                return ResponseEntity.ok(ApiResponseWpp.success(user, "User information retrieved"));
        }

        // ==========================================
        // Admin: Password Reset
        // ==========================================

        @Operation(summary = "Admin: Reset user password", description = "Allows an ADMIN to forcefully reset any user's password. ADMIN role required.", security = @SecurityRequirement(name = "bearerAuth"))
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Password reset successfully"),
                        @ApiResponse(responseCode = "400", description = "Validation error"),
                        @ApiResponse(responseCode = "403", description = "Forbidden — ADMIN role required"),
                        @ApiResponse(responseCode = "404", description = "User not found")
        })
        @PreAuthorize("hasRole('ADMIN')")
        @PostMapping("/admin/reset-password/{userId}")
        public ResponseEntity<ApiResponseWpp<Void>> adminResetPassword(
                        @Parameter(description = "ID of the user whose password will be reset", required = true, example = "5") @PathVariable Long userId,
                        @Valid @RequestBody AdminPasswordResetDto request) {

                log.info("Admin password reset request for user id: {}", userId);
                authenticationService.adminResetPassword(userId, request.getNewPassword());
                return ResponseEntity.ok(ApiResponseWpp.success(null, "Password reset successfully"));
        }
}