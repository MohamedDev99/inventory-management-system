package com.moeware.ims.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moeware.ims.dto.ApiResponseWpp;
import com.moeware.ims.dto.auth.AuthenticationRequest;
import com.moeware.ims.dto.auth.AuthenticationResponse;
import com.moeware.ims.dto.auth.RefreshTokenRequest;
import com.moeware.ims.dto.user.UserRegistrationDto;
import com.moeware.ims.dto.user.UserResponseDto;
import com.moeware.ims.exception.GlobalExceptionHandler;
import com.moeware.ims.service.AuthenticationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
@Tag(name = "Authentication", description = "Authentication management APIs for user registration, login, and token operations")
public class AuthController {

        private final AuthenticationService authenticationService;

        /**
         * Register a new user
         * POST /api/auth/register
         */
        @Operation(summary = "Register a new user", description = "Create a new user account with username, email, password, and role. Returns JWT tokens upon successful registration.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "User registered successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthenticationResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(mediaType = "application/json", schema = @Schema(implementation = GlobalExceptionHandler.ValidationErrorResponse.class))),
                        @ApiResponse(responseCode = "409", description = "User already exists (username or email conflict)", content = @Content(mediaType = "application/json", schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class)))
        })
        @PostMapping("/register")
        public ResponseEntity<ApiResponseWpp<AuthenticationResponse>> register(
                        @Valid @RequestBody UserRegistrationDto request) {

                log.info("Registration request for username: {}", request.getUsername());

                AuthenticationResponse response = authenticationService.register(request);

                return ResponseEntity
                                .status(HttpStatus.CREATED)
                                .body(ApiResponseWpp.success(response, "User registered successfully"));
        }

        /**
         * Authenticate user and generate tokens
         * POST /api/auth/login
         */
        @Operation(summary = "Authenticate user", description = "Login with username and password. Returns access token and refresh token on successful authentication.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Login successful", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthenticationResponse.class))),
                        @ApiResponse(responseCode = "401", description = "Invalid credentials", content = @Content(mediaType = "application/json", schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class)))
        })
        @PostMapping("/login")
        public ResponseEntity<ApiResponseWpp<AuthenticationResponse>> login(
                        @Valid @RequestBody AuthenticationRequest request) {

                log.info("Login request for username: {}", request.getUsername());

                AuthenticationResponse response = authenticationService.authenticate(request);

                return ResponseEntity.ok(
                                ApiResponseWpp.success(response, "Login successful"));
        }

        /**
         * Refresh access token using refresh token
         * POST /api/auth/refresh
         */
        @Operation(summary = "Refresh access token", description = "Generate a new access token using a valid refresh token. The refresh token remains the same.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Token refreshed successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthenticationResponse.class))),
                        @ApiResponse(responseCode = "401", description = "Invalid or expired refresh token", content = @Content(mediaType = "application/json", schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class)))
        })
        @PostMapping("/refresh")
        public ResponseEntity<ApiResponseWpp<AuthenticationResponse>> refreshToken(
                        @Valid @RequestBody RefreshTokenRequest request) {

                log.info("Token refresh request");

                AuthenticationResponse response = authenticationService.refreshToken(request);

                return ResponseEntity.ok(
                                ApiResponseWpp.success(response, "Token refreshed successfully"));
        }

        /**
         * Logout user (client-side token removal)
         * POST /api/auth/logout
         */
        @Operation(summary = "Logout user", description = "Logout user (client-side token removal). In stateless JWT, this is handled client-side.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Logout successful", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class)))
        })
        @PostMapping("/logout")
        public ResponseEntity<ApiResponseWpp<Void>> logout() {
                log.info("Logout request");

                // In a stateless JWT setup, logout is handled client-side by removing the token
                // Optionally, you can implement token blacklisting here

                return ResponseEntity.ok(
                                ApiResponseWpp.success(null, "Logout successful"));
        }

        /**
         * Validate if a token is still valid
         * POST /api/auth/validate
         */
        @Operation(summary = "Validate JWT token", description = "Check if the provided JWT token is valid and not expired.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Token validation result", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class)))
        })
        @Parameter(name = "Authorization", description = "JWT token with Bearer prefix", required = true, example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        @PostMapping("/validate")
        public ResponseEntity<ApiResponseWpp<Boolean>> validateToken(
                        @RequestHeader("Authorization") String authHeader) {

                log.info("Token validation request");

                boolean isValid = authenticationService.validateToken(authHeader);

                return ResponseEntity.ok(
                                ApiResponseWpp.success(isValid, "Token validation complete"));
        }

        /**
         * Get current authenticated user info
         * GET /api/auth/me
         */
        @Operation(summary = "Get current user information", description = "Retrieve information about the currently authenticated user.", security = @SecurityRequirement(name = "bearerAuth"))
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "User information retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDto.class))),
                        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token", content = @Content(mediaType = "application/json", schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class)))
        })
        @GetMapping("/me")
        public ResponseEntity<ApiResponseWpp<UserResponseDto>> getCurrentUser() {
                log.info("Get current user request");

                UserResponseDto user = authenticationService.getCurrentUser();

                return ResponseEntity.ok(
                                ApiResponseWpp.success(user, "User information retrieved"));
        }
}