package com.moeware.ims.dto.user;

import com.moeware.ims.entity.User;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for user response (hides sensitive information)
 *
 * @author MoeWare Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "User information response (excludes sensitive data)")
public class UserResponseDto {

    @Schema(description = "Unique user identifier", example = "1")
    private Long id;

    @Schema(description = "Username", example = "johndoe")
    private String username;

    @Schema(description = "Email address", example = "john.doe@example.com")
    private String email;

    @Schema(description = "User role name", example = "MANAGER")
    private String roleName;

    @Schema(description = "Account active status", example = "true")
    private Boolean isActive;

    @Schema(description = "Last login timestamp", example = "2026-01-28T10:30:00")
    private LocalDateTime lastLogin;

    @Schema(description = "Account creation timestamp", example = "2026-01-20T09:15:00")
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp", example = "2026-01-28T10:30:00")
    private LocalDateTime updatedAt;

    /**
     * Convert User entity to DTO
     */
    public static UserResponseDto fromEntity(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .roleName(user.getRole() != null ? user.getRole().getName() : null)
                .isActive(user.getIsActive())
                .lastLogin(user.getLastLogin())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}