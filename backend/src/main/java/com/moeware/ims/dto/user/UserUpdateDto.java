package com.moeware.ims.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for updating user information
 * All fields are optional
 *
 * @author MoeWare Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User update request (all fields optional)")
public class UserUpdateDto {

    @Schema(description = "New email address", example = "newemail@example.com")
    @Email(message = "Email must be valid")
    private String email;

    @Schema(description = "New role name", example = "MANAGER", allowableValues = { "ADMIN", "MANAGER",
            "WAREHOUSE_STAFF", "VIEWER" })
    private String roleName;
}