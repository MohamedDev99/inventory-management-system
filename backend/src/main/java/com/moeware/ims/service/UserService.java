package com.moeware.ims.service;

import com.moeware.ims.dto.user.UserRegistrationDto;
import com.moeware.ims.dto.user.UserResponseDto;
import com.moeware.ims.dto.user.UserUpdateDto;
import com.moeware.ims.entity.Role;
import com.moeware.ims.entity.User;
import com.moeware.ims.exception.ResourceNotFoundException;
import com.moeware.ims.exception.user.UserAlreadyExistsException;
import com.moeware.ims.repository.RoleRepository;
import com.moeware.ims.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for managing users
 * Also implements UserDetailsService for Spring Security
 *
 * @author MoeWare Team
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Load user by username (required by Spring Security)
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Loading user by username: {}", username);
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    /**
     * Register a new user
     */
    @Transactional
    public UserResponseDto registerUser(UserRegistrationDto registrationDto) {
        log.info("Registering new user: {}", registrationDto.getUsername());

        // Check if username already exists
        if (userRepository.existsByUsername(registrationDto.getUsername())) {
            throw new UserAlreadyExistsException("Username already exists: " + registrationDto.getUsername());
        }

        // Check if email already exists
        if (userRepository.existsByEmail(registrationDto.getEmail())) {
            throw new UserAlreadyExistsException("Email already exists: " + registrationDto.getEmail());
        }

        // Get role (default to VIEWER if not specified)
        String roleName = registrationDto.getRoleName() != null ? registrationDto.getRoleName() : "VIEWER";

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + roleName));

        // Create user entity
        User user = User.builder()
                .username(registrationDto.getUsername())
                .email(registrationDto.getEmail())
                .passwordHash(passwordEncoder.encode(registrationDto.getPassword()))
                .role(role)
                .isActive(true)
                .build();

        User savedUser = userRepository.save(user);
        log.info("User registered successfully: {}", savedUser.getUsername());

        return UserResponseDto.fromEntity(savedUser);
    }

    /**
     * Get user by ID
     */
    public UserResponseDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return UserResponseDto.fromEntity(user);
    }

    /**
     * Get user passwordhash
     * 
     * @param id
     * @return
     */
    public String getUserPasswordHash(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return user.getPasswordHash();
    }

    /**
     * Get user by username
     */
    public UserResponseDto getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
        return UserResponseDto.fromEntity(user);
    }

    /**
     * Get all active users with pagination
     */
    public Page<UserResponseDto> getAllActiveUsers(Pageable pageable) {
        return userRepository.findByIsActiveTrue(pageable)
                .map(UserResponseDto::fromEntity);
    }

    /**
     * Search users by username or email
     */
    public Page<UserResponseDto> searchUsers(String searchTerm, Pageable pageable) {
        return userRepository.searchUsers(searchTerm, pageable)
                .map(UserResponseDto::fromEntity);
    }

    /**
     * Update user information
     */
    @Transactional
    public UserResponseDto updateUser(Long id, UserUpdateDto updateDto) {
        log.info("Updating user with id: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        // Update email if provided and different
        if (updateDto.getEmail() != null && !updateDto.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(updateDto.getEmail())) {
                throw new UserAlreadyExistsException("Email already exists: " + updateDto.getEmail());
            }
            user.setEmail(updateDto.getEmail());
        }

        // Update role if provided
        if (updateDto.getRoleName() != null) {
            Role role = roleRepository.findByName(updateDto.getRoleName())
                    .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + updateDto.getRoleName()));
            user.setRole(role);
        }

        User updatedUser = userRepository.save(user);
        log.info("User updated successfully: {}", updatedUser.getUsername());

        return UserResponseDto.fromEntity(updatedUser);
    }

    /**
     * Change user password
     */
    @Transactional
    public void changePassword(Long id, String newPassword) {
        log.info("Changing password for user id: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        log.info("Password changed successfully for user: {}", user.getUsername());
    }

    /**
     * Deactivate user (soft delete)
     */
    @Transactional
    public void deactivateUser(Long id) {
        log.info("Deactivating user with id: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        user.deactivate();
        userRepository.save(user);

        log.info("User deactivated successfully: {}", user.getUsername());
    }

    /**
     * Activate user
     */
    @Transactional
    public void activateUser(Long id) {
        log.info("Activating user with id: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        user.activate();
        userRepository.save(user);

        log.info("User activated successfully: {}", user.getUsername());
    }

    /**
     * Update last login timestamp
     */
    @Transactional
    public void updateLastLogin(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        user.updateLastLogin();
        userRepository.save(user);
    }

    /**
     * Get user count statistics
     */
    public UserStatistics getStatistics() {
        long totalUsers = userRepository.count();
        long activeUsers = userRepository.countByIsActiveTrue();
        long adminCount = userRepository.countByRoleName("ADMIN");
        long managerCount = userRepository.countByRoleName("MANAGER");
        long staffCount = userRepository.countByRoleName("WAREHOUSE_STAFF");
        long viewerCount = userRepository.countByRoleName("VIEWER");

        return new UserStatistics(totalUsers, activeUsers, adminCount,
                managerCount, staffCount, viewerCount);
    }

    /**
     * Statistics record
     */
    public record UserStatistics(
            long totalUsers,
            long activeUsers,
            long adminCount,
            long managerCount,
            long staffCount,
            long viewerCount) {
    }
}