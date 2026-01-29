package com.moeware.ims.controller;

import com.moeware.ims.dto.ApiResponseWpp;
import com.moeware.ims.dto.auth.ChangePasswordDto;
import com.moeware.ims.dto.user.UserResponseDto;
import com.moeware.ims.dto.user.UserUpdateDto;
import com.moeware.ims.exception.GlobalExceptionHandler;
import com.moeware.ims.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * REST controller for user management operations
 *
 * @author MoeWare Team
 * @version 1.0
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "User Management", description = "User management APIs for CRUD operations, password changes, and user statistics")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

        private final UserService userService;

        /**
         * Get all active users with pagination
         * GET /api/users
         * Requires ADMIN or MANAGER role
         */
        @Operation(summary = "Get all active users", description = "Retrieve a paginated list of all active users. Requires ADMIN or MANAGER role.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Users retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))),
                        @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions", content = @Content(mediaType = "application/json", schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class)))
        })
        @Parameters({
                        @Parameter(name = "page", description = "Page number (0-indexed)", example = "0"),
                        @Parameter(name = "size", description = "Number of items per page", example = "10"),
                        @Parameter(name = "sortBy", description = "Field to sort by", example = "createdAt"),
                        @Parameter(name = "direction", description = "Sort direction (ASC or DESC)", example = "DESC")
        })
        @GetMapping
        @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
        public ResponseEntity<ApiResponseWpp<Page<UserResponseDto>>> getAllUsers(
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size,
                        @RequestParam(defaultValue = "createdAt") String sortBy,
                        @RequestParam(defaultValue = "DESC") String direction) {

                log.info("Get all users request - page: {}, size: {}", page, size);

                Sort.Direction sortDirection = Sort.Direction.fromString(direction);
                Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

                Page<UserResponseDto> users = userService.getAllActiveUsers(pageable);

                return ResponseEntity.ok(
                                ApiResponseWpp.success(users, "Users retrieved successfully"));
        }

        /**
         * Search users by username or email
         * GET /api/users/search
         * Requires ADMIN or MANAGER role
         */
        @Operation(summary = "Search users", description = "Search users by username or email with pagination. Requires ADMIN or MANAGER role.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Search completed successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))),
                        @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions")
        })
        @Parameters({
                        @Parameter(name = "query", description = "Search term for username or email", required = true, example = "john"),
                        @Parameter(name = "page", description = "Page number (0-indexed)", example = "0"),
                        @Parameter(name = "size", description = "Number of items per page", example = "10")
        })
        @GetMapping("/search")
        @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
        public ResponseEntity<ApiResponseWpp<Page<UserResponseDto>>> searchUsers(
                        @RequestParam String query,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size) {

                log.info("Search users request - query: {}", query);

                Pageable pageable = PageRequest.of(page, size);
                Page<UserResponseDto> users = userService.searchUsers(query, pageable);

                return ResponseEntity.ok(
                                ApiResponseWpp.success(users, "Search completed"));
        }

        /**
         * Get user by ID
         * GET /api/users/{id}
         * Requires ADMIN or MANAGER role
         */
        @Operation(summary = "Get user by ID", description = "Retrieve detailed information about a specific user by their ID. Requires ADMIN or MANAGER role.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "User retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDto.class))),
                        @ApiResponse(responseCode = "404", description = "User not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class))),
                        @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions")
        })
        @Parameter(name = "id", description = "User ID", required = true, example = "1")
        @GetMapping("/{id}")
        @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
        public ResponseEntity<ApiResponseWpp<UserResponseDto>> getUserById(@PathVariable Long id) {
                log.info("Get user by id request - id: {}", id);

                UserResponseDto user = userService.getUserById(id);

                return ResponseEntity.ok(
                                ApiResponseWpp.success(user, "User retrieved successfully"));
        }

        /**
         * Update user information
         * PUT /api/users/{id}
         * Requires ADMIN role
         */
        @Operation(summary = "Update user information", description = "Update user's email or role. Requires ADMIN role.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "User updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDto.class))),
                        @ApiResponse(responseCode = "404", description = "User not found"),
                        @ApiResponse(responseCode = "409", description = "Email already exists"),
                        @ApiResponse(responseCode = "403", description = "Forbidden - Requires ADMIN role")
        })
        @Parameter(name = "id", description = "User ID to update", required = true, example = "1")
        @PutMapping("/{id}")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<ApiResponseWpp<UserResponseDto>> updateUser(
                        @PathVariable Long id,
                        @Valid @RequestBody UserUpdateDto updateDto) {

                log.info("Update user request - id: {}", id);

                UserResponseDto user = userService.updateUser(id, updateDto);

                return ResponseEntity.ok(
                                ApiResponseWpp.success(user, "User updated successfully"));
        }

        /**
         * Change user password
         * POST /api/users/{id}/change-password
         * Users can change their own password, ADMIN can change any
         */
        @Operation(summary = "Change user password", description = "Change password for a user. Users can change their own password, ADMIN can change any user's password.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Password changed successfully"),
                        @ApiResponse(responseCode = "400", description = "Passwords do not match or invalid input"),
                        @ApiResponse(responseCode = "403", description = "Forbidden - Can only change own password unless ADMIN"),
                        @ApiResponse(responseCode = "404", description = "User not found")
        })
        @Parameter(name = "id", description = "User ID", required = true, example = "1")
        @PostMapping("/{id}/change-password")
        public ResponseEntity<ApiResponseWpp<Void>> changePassword(
                        @PathVariable Long id,
                        @Valid @RequestBody ChangePasswordDto changePasswordDto,
                        Authentication authentication) {

                log.info("Change password request - user id: {}", id);

                // Check if user is changing their own password or is admin
                UserResponseDto currentUser = userService.getUserByUsername(authentication.getName());
                boolean isAdmin = authentication.getAuthorities().stream()
                                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

                if (!currentUser.getId().equals(id) && !isAdmin) {
                        return ResponseEntity.status(403).body(
                                        ApiResponseWpp.error("You can only change your own password"));
                }

                // Verify passwords match
                if (!changePasswordDto.passwordsMatch()) {
                        return ResponseEntity.badRequest().body(
                                        ApiResponseWpp.error("Passwords do not match"));
                }

                userService.changePassword(id, changePasswordDto.getNewPassword());

                return ResponseEntity.ok(
                                ApiResponseWpp.success(null, "Password changed successfully"));
        }

        /**
         * Deactivate user (soft delete)
         * DELETE /api/users/{id}
         * Requires ADMIN role
         */
        @Operation(summary = "Deactivate user", description = "Soft delete a user by setting their account to inactive. Requires ADMIN role.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "User deactivated successfully"),
                        @ApiResponse(responseCode = "404", description = "User not found"),
                        @ApiResponse(responseCode = "403", description = "Forbidden - Requires ADMIN role")
        })
        @Parameter(name = "id", description = "User ID to deactivate", required = true, example = "1")
        @DeleteMapping("/{id}")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<ApiResponseWpp<Void>> deactivateUser(@PathVariable Long id) {
                log.info("Deactivate user request - id: {}", id);

                userService.deactivateUser(id);

                return ResponseEntity.ok(
                                ApiResponseWpp.success(null, "User deactivated successfully"));
        }

        /**
         * Activate user
         * POST /api/users/{id}/activate
         * Requires ADMIN role
         */
        @Operation(summary = "Activate user", description = "Reactivate a previously deactivated user account. Requires ADMIN role.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "User activated successfully"),
                        @ApiResponse(responseCode = "404", description = "User not found"),
                        @ApiResponse(responseCode = "403", description = "Forbidden - Requires ADMIN role")
        })
        @Parameter(name = "id", description = "User ID to activate", required = true, example = "1")
        @PostMapping("/{id}/activate")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<ApiResponseWpp<Void>> activateUser(@PathVariable Long id) {
                log.info("Activate user request - id: {}", id);

                userService.activateUser(id);

                return ResponseEntity.ok(
                                ApiResponseWpp.success(null, "User activated successfully"));
        }

        /**
         * Get user statistics
         * GET /api/users/statistics
         * Requires ADMIN role
         */
        @Operation(summary = "Get user statistics", description = "Retrieve statistics about users including total count, active users, and counts by role. Requires ADMIN role.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserService.UserStatistics.class))),
                        @ApiResponse(responseCode = "403", description = "Forbidden - Requires ADMIN role")
        })
        @GetMapping("/statistics")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<ApiResponseWpp<UserService.UserStatistics>> getUserStatistics() {
                log.info("Get user statistics request");

                UserService.UserStatistics statistics = userService.getStatistics();

                return ResponseEntity.ok(
                                ApiResponseWpp.success(statistics, "Statistics retrieved successfully"));
        }
}