package com.moeware.ims.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.moeware.ims.dto.ApiResponseWpp;
import com.moeware.ims.dto.auth.ChangePasswordDto;
import com.moeware.ims.dto.user.UserRegistrationDto;
import com.moeware.ims.dto.user.UserResponseDto;
import com.moeware.ims.dto.user.UserUpdateDto;
import com.moeware.ims.exception.auth.InvalidCredentialsException;
import com.moeware.ims.exception.handler.GlobalExceptionHandler;
import com.moeware.ims.service.UserService;

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
 * REST controller for User management.
 *
 * <p>
 * <b>Role access matrix:</b>
 * </p>
 * <ul>
 * <li>ADMIN – full access (create, read, update, delete, activate, deactivate,
 * statistics)</li>
 * <li>MANAGER – read all users, update their own profile, change own
 * password</li>
 * <li>WAREHOUSE_STAFF – read their own profile, change own password</li>
 * <li>VIEWER – read their own profile</li>
 * </ul>
 *
 * @author MoeWare Team
 * @version 1.0
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Users", description = "User management APIs — CRUD, role management, and account status control")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

        private final UserService userService;

        // ==========================================
        // Create User (ADMIN only)
        // ==========================================

        @Operation(summary = "Create a new user", description = "Register a new user with a specified role. Only ADMIN can create users this way. "
                        + "For self-registration use POST /api/auth/register.")
        @ApiResponses({
                        @ApiResponse(responseCode = "201", description = "User created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDto.class))),
                        @ApiResponse(responseCode = "400", description = "Validation error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = GlobalExceptionHandler.ValidationErrorResponse.class))),
                        @ApiResponse(responseCode = "403", description = "Forbidden — ADMIN role required"),
                        @ApiResponse(responseCode = "409", description = "Username or email already exists", content = @Content(mediaType = "application/json", schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class)))
        })
        @PreAuthorize("hasRole('ADMIN')")
        @PostMapping
        public ResponseEntity<ApiResponseWpp<UserResponseDto>> createUser(
                        @Valid @RequestBody UserRegistrationDto request) {

                log.info("Admin creating user: {}", request.getUsername());
                UserResponseDto user = userService.registerUser(request);
                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(ApiResponseWpp.success(user, "User created successfully"));
        }

        // ==========================================
        // Get All Users (ADMIN, MANAGER)
        // ==========================================

        @Operation(summary = "List all active users", description = "Returns a paginated list of all active users. Accessible by ADMIN and MANAGER.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
                        @ApiResponse(responseCode = "403", description = "Forbidden")
        })
        @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
        @GetMapping
        public ResponseEntity<ApiResponseWpp<Page<UserResponseDto>>> getAllUsers(
                        @PageableDefault(size = 20, sort = "id") Pageable pageable) {

                log.info("List all active users, page: {}", pageable.getPageNumber());
                Page<UserResponseDto> users = userService.getAllActiveUsers(pageable);
                return ResponseEntity.ok(ApiResponseWpp.success(users, "Users retrieved successfully"));
        }

        // ==========================================
        // Search Users (ADMIN, MANAGER)
        // ==========================================

        @Operation(summary = "Search users by username or email", description = "Case-insensitive search across username and email. Accessible by ADMIN and MANAGER.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Search results returned"),
                        @ApiResponse(responseCode = "403", description = "Forbidden")
        })
        @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
        @GetMapping("/search")
        public ResponseEntity<ApiResponseWpp<Page<UserResponseDto>>> searchUsers(
                        @Parameter(description = "Search term (username or email)", example = "john") @RequestParam String q,
                        @PageableDefault(size = 20) Pageable pageable) {

                log.info("Search users with term: {}", q);
                Page<UserResponseDto> results = userService.searchUsers(q, pageable);
                return ResponseEntity.ok(ApiResponseWpp.success(results, "Search completed"));
        }

        // ==========================================
        // Get User Statistics (ADMIN only)
        // ==========================================

        @Operation(summary = "Get user statistics", description = "Returns total users, active users, and counts per role. ADMIN only.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Statistics retrieved"),
                        @ApiResponse(responseCode = "403", description = "Forbidden — ADMIN role required")
        })
        @PreAuthorize("hasRole('ADMIN')")
        @GetMapping("/statistics")
        public ResponseEntity<ApiResponseWpp<UserService.UserStatistics>> getStatistics() {
                log.info("Get user statistics");
                UserService.UserStatistics stats = userService.getStatistics();
                return ResponseEntity.ok(ApiResponseWpp.success(stats, "Statistics retrieved"));
        }

        // ==========================================
        // Get User by ID (ADMIN, MANAGER, or self)
        // ==========================================

        @Operation(summary = "Get user by ID", description = "Retrieve a specific user's details. "
                        + "ADMIN and MANAGER can view any user; other roles can only view their own profile.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "User found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDto.class))),
                        @ApiResponse(responseCode = "403", description = "Forbidden"),
                        @ApiResponse(responseCode = "404", description = "User not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class)))
        })
        @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or @userSecurityService.isSelf(#id, authentication)")
        @GetMapping("/{id}")
        public ResponseEntity<ApiResponseWpp<UserResponseDto>> getUserById(
                        @Parameter(description = "User ID", required = true, example = "1") @PathVariable Long id) {

                log.info("Get user by id: {}", id);
                UserResponseDto user = userService.getUserById(id);
                return ResponseEntity.ok(ApiResponseWpp.success(user, "User retrieved successfully"));
        }

        // ==========================================
        // Update User (ADMIN, or self for MANAGER)
        // ==========================================

        @Operation(summary = "Update user profile", description = "Update a user's email or role. "
                        + "ADMIN can update any user (including role). "
                        + "MANAGER can update their own profile (email only, role change requires ADMIN).")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "User updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDto.class))),
                        @ApiResponse(responseCode = "400", description = "Validation error"),
                        @ApiResponse(responseCode = "403", description = "Forbidden"),
                        @ApiResponse(responseCode = "404", description = "User not found")
        })
        @PreAuthorize("hasRole('ADMIN') or (hasRole('MANAGER') and @userSecurityService.isSelf(#id, authentication))")
        @PutMapping("/{id}")
        public ResponseEntity<ApiResponseWpp<UserResponseDto>> updateUser(
                        @Parameter(description = "User ID", required = true, example = "1") @PathVariable Long id,
                        @Valid @RequestBody UserUpdateDto updateDto,
                        Authentication authentication) {

                log.info("Update user id: {} by {}", id, authentication.getName());

                // Non-ADMIN users cannot change roles
                boolean isAdmin = authentication.getAuthorities().stream()
                                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
                if (!isAdmin && updateDto.getRoleName() != null) {
                        throw new InvalidCredentialsException("Only ADMIN can change user roles");
                }

                UserResponseDto updated = userService.updateUser(id, updateDto);
                return ResponseEntity.ok(ApiResponseWpp.success(updated, "User updated successfully"));
        }

        // ==========================================
        // Change Own Password (authenticated users)
        // ==========================================

        @Operation(summary = "Change own password", description = "Allows any authenticated user to change their own password. "
                        + "Requires the current password for verification.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Password changed successfully"),
                        @ApiResponse(responseCode = "400", description = "Validation error or wrong current password"),
                        @ApiResponse(responseCode = "403", description = "Forbidden — can only change own password")
        })
        @PreAuthorize("@userSecurityService.isSelf(#id, authentication)")
        @PostMapping("/{id}/change-password")
        public ResponseEntity<ApiResponseWpp<Void>> changePassword(
                        @Parameter(description = "User ID", required = true, example = "1") @PathVariable Long id,
                        @Valid @RequestBody ChangePasswordDto request) {

                log.info("Change password request for user id: {}", id);
                userService.changePasswordWithVerification(id, request.getCurrentPassword(), request.getNewPassword());
                return ResponseEntity.ok(ApiResponseWpp.success(null, "Password changed successfully"));
        }

        // ==========================================
        // Deactivate User / Soft Delete (ADMIN only)
        // ==========================================

        @Operation(summary = "Deactivate user", description = "Soft-delete a user by marking them inactive. The user can no longer log in. ADMIN only.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "User deactivated"),
                        @ApiResponse(responseCode = "403", description = "Forbidden — ADMIN role required"),
                        @ApiResponse(responseCode = "404", description = "User not found")
        })
        @PreAuthorize("hasRole('ADMIN')")
        @DeleteMapping("/{id}")
        public ResponseEntity<ApiResponseWpp<Void>> deactivateUser(
                        @Parameter(description = "User ID", required = true, example = "1") @PathVariable Long id) {

                log.info("Deactivate user id: {}", id);
                userService.deactivateUser(id);
                return ResponseEntity.ok(ApiResponseWpp.success(null, "User deactivated successfully"));
        }

        // ==========================================
        // Activate User (ADMIN only)
        // ==========================================

        @Operation(summary = "Activate user", description = "Re-activate a previously deactivated user. ADMIN only.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "User activated"),
                        @ApiResponse(responseCode = "403", description = "Forbidden — ADMIN role required"),
                        @ApiResponse(responseCode = "404", description = "User not found")
        })
        @PreAuthorize("hasRole('ADMIN')")
        @PatchMapping("/{id}/activate")
        public ResponseEntity<ApiResponseWpp<Void>> activateUser(
                        @Parameter(description = "User ID", required = true, example = "1") @PathVariable Long id) {

                log.info("Activate user id: {}", id);
                userService.activateUser(id);
                return ResponseEntity.ok(ApiResponseWpp.success(null, "User activated successfully"));
        }

        // ==========================================
        // Unlock User (ADMIN only)
        // ==========================================

        @Operation(summary = "Unlock user account", description = "Manually unlock a user account that was locked due to failed login attempts. ADMIN only.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "User account unlocked"),
                        @ApiResponse(responseCode = "403", description = "Forbidden — ADMIN role required"),
                        @ApiResponse(responseCode = "404", description = "User not found")
        })
        @PreAuthorize("hasRole('ADMIN')")
        @PatchMapping("/{id}/unlock")
        public ResponseEntity<ApiResponseWpp<Void>> unlockUser(
                        @Parameter(description = "User ID", required = true, example = "1") @PathVariable Long id) {

                log.info("Unlock user id: {}", id);
                userService.unlockUser(id);
                return ResponseEntity.ok(ApiResponseWpp.success(null, "User account unlocked successfully"));
        }
}