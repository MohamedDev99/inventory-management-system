package com.moeware.ims.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

/**
 * User entity implementing Spring Security UserDetails
 *
 * @author MoeWare Team
 * @version 1.0
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
@Schema(description = "User entity with authentication and authorization details")
public class User extends VersionedEntity implements UserDetails {

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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    @Schema(description = "User's role", example = "MANAGER")
    private Role role;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    @Schema(description = "Account active status (false = soft deleted)", example = "true", defaultValue = "true")
    private Boolean isActive = true;

    @Column(name = "last_login")
    @Schema(description = "Last successful login timestamp", example = "2026-01-28T10:30:00", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime lastLogin;

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

    @Override
    public boolean isAccountNonLocked() {
        return isActive;
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

    /**
     * Update last login timestamp
     */
    public void updateLastLogin() {
        this.lastLogin = LocalDateTime.now();
    }

    /**
     * Check if user has admin role
     */
    public boolean isAdmin() {
        return role != null && role.isAdmin();
    }

    /**
     * Check if user has manager role
     */
    public boolean isManager() {
        return role != null && role.isManager();
    }

    /**
     * Deactivate user account (soft delete)
     */
    public void deactivate() {
        this.isActive = false;
    }

    /**
     * Activate user account
     */
    public void activate() {
        this.isActive = true;
    }
}