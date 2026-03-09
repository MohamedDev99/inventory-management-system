package com.moeware.ims.dto.staff.employee;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for linking an employee to a system user account
 *
 * @author MoeWare Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to link an employee to a system user account")
public class LinkUserDTO {

    @Schema(description = "ID of the system user to link to this employee", example = "25", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "User ID is required")
    private Long userId;
}