package com.moeware.ims.entity;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * User entity implementing Spring Security UserDetails.
 * Includes account lockout support (failed login tracking) and soft-delete via
 * isActive.
 *
 * @author MoeWare Team
 * @version 1.1
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "passwordHash")
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Schema(description = "User entity with authentication, authorization, and lockout details")
public class User extends VersionedEntity implements UserDetails {

    // ==========================================
    // Constants
    // ==========================================

    /** Maximum consecutive failed login attempts before account is locked */
    public static final int MAX_FAILED_ATTEMPTS = 5;

    /**
     * Duration (in minutes) an account remains locked after exceeding failed
     * attempts
     */
    public static final int LOCK_DURATION_MINUTES = 30;

    // ==========================================
    // Identity
    // ==========================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @Schema(description = "Unique user identifier", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Column(name = "username", nullable = false, unique = true, length = 50)
    @Schema(description = "Unique username (3-50 characters)", example = "johndoe", requiredMode = Schema.RequiredMode.REQUIRED, minLength = 3, maxLength = 50)
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Column(name = "email", nullable = false, unique = true)
    @Schema(description = "Valid email address", example = "john.doe@example.com", requiredMode = Schema.RequiredMode.REQUIRED, format = "email")
    private String email;

    @NotBlank(message = "Password is required")
    @Column(name = "password_hash", nullable = false)
    @Schema(description = "BCrypt hashed password", accessMode = Schema.AccessMode.WRITE_ONLY, format = "password")
    private String passwordHash;

    // ==========================================
    // Role
    // ==========================================

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    @Schema(description = "User's role", example = "MANAGER")
    private Role role;

    // ==========================================
    // Account Status
    // ==========================================

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    @Schema(description = "Account active status (false = soft deleted)", example = "true", defaultValue = "true")
    private Boolean isActive = true;

    @Column(name = "last_login")
    @Schema(description = "Last successful login timestamp", example = "2026-01-28T10:30:00", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime lastLogin;

    // ==========================================
    // Account Lockout
    // ==========================================

    @Column(name = "failed_login_attempts", nullable = false)
    @Builder.Default
    @Schema(description = "Number of consecutive failed login attempts", example = "0", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer failedLoginAttempts = 0;

    @Column(name = "locked_until")
    @Schema(description = "Timestamp until which the account is locked (null = not locked)", example = "2026-01-28T11:00:00", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime lockedUntil;

    // ==========================================
    // UserDetails Implementation
    // ==========================================

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + role.getName()));
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Account is non-locked when:
     * - lockedUntil is null (never locked), OR
     * - lockedUntil is in the past (lock has expired)
     */
    @Override
    public boolean isAccountNonLocked() {
        if (lockedUntil == null) {
            return true;
        }
        return LocalDateTime.now().isAfter(lockedUntil);
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isActive;
    }

    // ==========================================
    // Convenience Methods
    // ==========================================

    /** Update last login timestamp */
    public void updateLastLogin() {
        this.lastLogin = LocalDateTime.now();
    }

    /** Check if user has admin role */
    public boolean isAdmin() {
        return role != null && role.isAdmin();
    }

    /** Check if user has manager role */
    public boolean isManager() {
        return role != null && role.isManager();
    }

    /** Deactivate user account (soft delete) */
    public void deactivate() {
        this.isActive = false;
    }

    /** Activate user account */
    public void activate() {
        this.isActive = true;
    }

    /**
     * Increment failed login counter.
     * Locks the account for LOCK_DURATION_MINUTES when MAX_FAILED_ATTEMPTS is
     * reached.
     */
    public void incrementFailedLoginAttempts() {
        this.failedLoginAttempts = (this.failedLoginAttempts == null ? 0 : this.failedLoginAttempts) + 1;
        if (this.failedLoginAttempts >= MAX_FAILED_ATTEMPTS) {
            this.lockedUntil = LocalDateTime.now().plusMinutes(LOCK_DURATION_MINUTES);
        }
    }

    /** Reset failed login counter and remove lock (called on successful login) */
    public void resetFailedLoginAttempts() {
        this.failedLoginAttempts = 0;
        this.lockedUntil = null;
    }

    /** Check if the account is currently locked */
    public boolean isCurrentlyLocked() {
        return lockedUntil != null && LocalDateTime.now().isBefore(lockedUntil);
    }
}