package com.moeware.ims.dto.staff.employee;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for assigning an employee to a department
 *
 * @author MoeWare Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to assign an employee to a department")
public class AssignDepartmentDTO {

    @Schema(description = "ID of the department to assign the employee to", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Department ID is required")
    private Long departmentId;
}