package com.moeware.ims.dto.staff.department;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for returning Department data in API responses
 *
 * @author MoeWare Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Department response with organizational details")
public class DepartmentResponseDTO {

    @Schema(description = "Unique identifier", example = "1")
    private Long id;

    @Schema(description = "Unique department code", example = "DEPT-001")
    private String departmentCode;

    @Schema(description = "Department name", example = "Warehouse Operations")
    private String name;

    @Schema(description = "Department description", example = "Manages all warehouse activities")
    private String description;

    @Schema(description = "Department manager summary")
    private ManagerSummary manager;

    @Schema(description = "Parent department summary (null if root department)")
    private ParentDepartmentSummary parentDepartment;

    @Schema(description = "Number of child departments", example = "3")
    private int childDepartmentCount;

    @Schema(description = "Cost center code for accounting", example = "CC-WH-001")
    private String costCenter;

    @Schema(description = "Number of employees in this department", example = "25")
    private int employeeCount;

    @Schema(description = "Whether the department is active", example = "true")
    private Boolean isActive;

    @Schema(description = "Whether this is a root (top-level) department", example = "true")
    private boolean rootDepartment;

    @Schema(description = "Timestamp when the department was created", example = "2024-01-10T00:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "Timestamp when the department was last updated", example = "2026-01-31T14:45:00")
    private LocalDateTime updatedAt;

    @Schema(description = "Username who created this department", example = "admin")
    private String createdBy;

    @Schema(description = "Username who last updated this department", example = "john.doe@company.com")
    private String updatedBy;

    // ==================== Nested Summary Classes ====================

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "Manager summary within a department response")
    public static class ManagerSummary {
        @Schema(description = "Employee ID", example = "10")
        private Long id;

        @Schema(description = "Employee code", example = "EMP-010")
        private String employeeCode;

        @Schema(description = "First name", example = "John")
        private String firstName;

        @Schema(description = "Last name", example = "Manager")
        private String lastName;

        @Schema(description = "Job title", example = "Warehouse Manager")
        private String jobTitle;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "Parent department summary within a department response")
    public static class ParentDepartmentSummary {
        @Schema(description = "Department ID", example = "1")
        private Long id;

        @Schema(description = "Department code", example = "DEPT-001")
        private String departmentCode;

        @Schema(description = "Department name", example = "Operations")
        private String name;
    }
}