package com.moeware.ims.dto.staff.department;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating or updating a Department
 *
 * @author MoeWare Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request payload for creating or updating a department")
public class DepartmentRequestDTO {

    @Schema(description = "Unique department code", example = "DEPT-001", requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 50)
    @NotBlank(message = "Department code is required")
    @Size(max = 50, message = "Department code must not exceed 50 characters")
    private String departmentCode;

    @Schema(description = "Department name", example = "Warehouse Operations", requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 100)
    @NotBlank(message = "Department name is required")
    @Size(max = 100, message = "Department name must not exceed 100 characters")
    private String name;

    @Schema(description = "Department description", example = "Manages all warehouse activities and inventory operations")
    private String description;

    @Schema(description = "ID of the employee who manages this department", example = "10")
    private Long managerId;

    @Schema(description = "ID of the parent department for hierarchical structure", example = "1")
    private Long parentDepartmentId;

    @Schema(description = "Cost center code for accounting", example = "CC-WH-001", maxLength = 50)
    @Size(max = 50, message = "Cost center must not exceed 50 characters")
    private String costCenter;

    @Schema(description = "Whether the department is active", example = "true")
    @Builder.Default
    private Boolean isActive = true;
}