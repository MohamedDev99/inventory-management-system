package com.moeware.ims.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for admin-initiated password reset.
 * Only accessible by ADMIN role via
 * {@code POST /api/auth/admin/reset-password/{userId}}.
 *
 * @author MoeWare Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Admin password reset request")
public class AdminPasswordResetDto {

    @Schema(description = "New password for the target user (min 8 characters)", example = "NewSecurePass123!", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "New password is required")
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    private String newPassword;
}