package com.moeware.ims.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for password change request
 *
 * @author MoeWare Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Password change request")
public class ChangePasswordDto {

    @Schema(description = "Current password", example = "OldPass123!", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Current password is required")
    private String currentPassword;

    @Schema(description = "New password (min 8 characters)", example = "NewSecurePass456!", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "New password is required")
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    private String newPassword;

    @Schema(description = "Confirmation of new password (must match newPassword)", example = "NewSecurePass456!", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Password confirmation is required")
    private String confirmPassword;

    /**
     * Check if new password and confirmation match
     */
    public boolean passwordsMatch() {
        return newPassword != null && newPassword.equals(confirmPassword);
    }
}