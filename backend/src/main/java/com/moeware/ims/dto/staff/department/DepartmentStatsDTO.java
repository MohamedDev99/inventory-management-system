package com.moeware.ims.dto.staff.department;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for returning Department statistics
 *
 * @author MoeWare Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Department statistics summary")
public class DepartmentStatsDTO {

    @Schema(description = "Department ID", example = "1")
    private Long departmentId;

    @Schema(description = "Department name", example = "Warehouse Operations")
    private String departmentName;

    @Schema(description = "Total number of employees in this department", example = "25")
    private int totalEmployees;

    @Schema(description = "Number of active employees", example = "23")
    private int activeEmployees;

    @Schema(description = "Number of inactive/terminated employees", example = "2")
    private int inactiveEmployees;

    @Schema(description = "Number of employees who have system access", example = "10")
    private int employeesWithSystemAccess;

    @Schema(description = "Number of direct child departments", example = "3")
    private int childDepartmentCount;

    @Schema(description = "Whether this department has a manager assigned", example = "true")
    private boolean hasManager;

    @Schema(description = "Whether this is a root department", example = "false")
    private boolean rootDepartment;
}