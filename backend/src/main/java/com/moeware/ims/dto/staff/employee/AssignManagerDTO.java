package com.moeware.ims.dto.staff.employee;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for assigning a manager to an employee
 *
 * @author MoeWare Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to assign a manager to an employee")
public class AssignManagerDTO {

    @Schema(description = "ID of the employee to set as manager", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Manager ID is required")
    private Long managerId;
}