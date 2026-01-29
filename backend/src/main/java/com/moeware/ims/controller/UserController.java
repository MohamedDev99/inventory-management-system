package com.moeware.ims.controller;

import com.moeware.ims.dto.ApiResponse;
import com.moeware.ims.dto.auth.ChangePasswordDto;
import com.moeware.ims.dto.user.UserResponseDto;
import com.moeware.ims.dto.user.UserUpdateDto;
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
public class UserController {

        private final UserService userService;

        /**
         * Get all active users with pagination
         * GET /api/users
         * Requires ADMIN or MANAGER role
         */
        @GetMapping
        @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
        public ResponseEntity<ApiResponse<Page<UserResponseDto>>> getAllUsers(
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size,
                        @RequestParam(defaultValue = "createdAt") String sortBy,
                        @RequestParam(defaultValue = "DESC") String direction) {

                log.info("Get all users request - page: {}, size: {}", page, size);

                Sort.Direction sortDirection = Sort.Direction.fromString(direction);
                Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

                Page<UserResponseDto> users = userService.getAllActiveUsers(pageable);

                return ResponseEntity.ok(
                                ApiResponse.success(users, "Users retrieved successfully"));
        }

        /**
         * Search users by username or email
         * GET /api/users/search
         * Requires ADMIN or MANAGER role
         */
        @GetMapping("/search")
        @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
        public ResponseEntity<ApiResponse<Page<UserResponseDto>>> searchUsers(
                        @RequestParam String query,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size) {

                log.info("Search users request - query: {}", query);

                Pageable pageable = PageRequest.of(page, size);
                Page<UserResponseDto> users = userService.searchUsers(query, pageable);

                return ResponseEntity.ok(
                                ApiResponse.success(users, "Search completed"));
        }

        /**
         * Get user by ID
         * GET /api/users/{id}
         * Requires ADMIN or MANAGER role
         */
        @GetMapping("/{id}")
        @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
        public ResponseEntity<ApiResponse<UserResponseDto>> getUserById(@PathVariable Long id) {
                log.info("Get user by id request - id: {}", id);

                UserResponseDto user = userService.getUserById(id);

                return ResponseEntity.ok(
                                ApiResponse.success(user, "User retrieved successfully"));
        }

        /**
         * Update user information
         * PUT /api/users/{id}
         * Requires ADMIN role
         */
        @PutMapping("/{id}")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<ApiResponse<UserResponseDto>> updateUser(
                        @PathVariable Long id,
                        @Valid @RequestBody UserUpdateDto updateDto) {

                log.info("Update user request - id: {}", id);

                UserResponseDto user = userService.updateUser(id, updateDto);

                return ResponseEntity.ok(
                                ApiResponse.success(user, "User updated successfully"));
        }

        /**
         * Change user password
         * POST /api/users/{id}/change-password
         * Users can change their own password, ADMIN can change any
         */
        @PostMapping("/{id}/change-password")
        public ResponseEntity<ApiResponse<Void>> changePassword(
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
                                        ApiResponse.error("You can only change your own password"));
                }

                // Verify passwords match
                if (!changePasswordDto.passwordsMatch()) {
                        return ResponseEntity.badRequest().body(
                                        ApiResponse.error("Passwords do not match"));
                }

                userService.changePassword(id, changePasswordDto.getNewPassword());

                return ResponseEntity.ok(
                                ApiResponse.success(null, "Password changed successfully"));
        }

        /**
         * Deactivate user (soft delete)
         * DELETE /api/users/{id}
         * Requires ADMIN role
         */
        @DeleteMapping("/{id}")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<ApiResponse<Void>> deactivateUser(@PathVariable Long id) {
                log.info("Deactivate user request - id: {}", id);

                userService.deactivateUser(id);

                return ResponseEntity.ok(
                                ApiResponse.success(null, "User deactivated successfully"));
        }

        /**
         * Activate user
         * POST /api/users/{id}/activate
         * Requires ADMIN role
         */
        @PostMapping("/{id}/activate")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<ApiResponse<Void>> activateUser(@PathVariable Long id) {
                log.info("Activate user request - id: {}", id);

                userService.activateUser(id);

                return ResponseEntity.ok(
                                ApiResponse.success(null, "User activated successfully"));
        }

        /**
         * Get user statistics
         * GET /api/users/statistics
         * Requires ADMIN role
         */
        @GetMapping("/statistics")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<ApiResponse<UserService.UserStatistics>> getUserStatistics() {
                log.info("Get user statistics request");

                UserService.UserStatistics statistics = userService.getStatistics();

                return ResponseEntity.ok(
                                ApiResponse.success(statistics, "Statistics retrieved successfully"));
        }
}