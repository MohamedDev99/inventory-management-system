package com.moeware.ims.dto.staff.employee;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for returning Employee data in API responses
 *
 * @author MoeWare Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Employee response with full job and organizational details")
public class EmployeeResponseDTO {

    @Schema(description = "Unique identifier", example = "10")
    private Long id;

    @Schema(description = "Unique employee code", example = "EMP-010")
    private String employeeCode;

    @Schema(description = "First name", example = "John")
    private String firstName;

    @Schema(description = "Last name", example = "Manager")
    private String lastName;

    @Schema(description = "Full name (firstName + lastName)", example = "John Manager")
    private String fullName;

    @Schema(description = "Work email address", example = "john@company.com")
    private String email;

    @Schema(description = "Contact phone number", example = "+1-555-0500")
    private String phone;

    @Schema(description = "Job title or position", example = "Warehouse Manager")
    private String jobTitle;

    @Schema(description = "Department assignment summary")
    private DepartmentSummary department;

    @Schema(description = "Direct manager summary (null if no manager)")
    private ManagerSummary manager;

    @Schema(description = "List of direct reports (subordinates)")
    private List<SubordinateSummary> directReports;

    @Schema(description = "Date of hire", example = "2023-01-15")
    private LocalDate hireDate;

    @Schema(description = "Date of termination (null if still active)", example = "2025-12-31")
    private LocalDate terminationDate;

    @Schema(description = "Employment tenure as human-readable string", example = "2 years, 1 month")
    private String tenure;

    @Schema(description = "Annual salary", example = "75000.00")
    private BigDecimal salary;

    @Schema(description = "Whether the employee is currently active", example = "true")
    private Boolean isActive;

    @Schema(description = "Whether the employee has been terminated", example = "false")
    private boolean terminated;

    @Schema(description = "Whether the employee has a linked system user account", example = "true")
    private boolean hasSystemAccess;

    @Schema(description = "Linked system user account summary (null if no user linked)")
    private UserSummary user;

    @Schema(description = "Timestamp when the employee record was created", example = "2024-01-15T10:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "Timestamp when the employee record was last updated", example = "2026-02-09T10:00:00")
    private LocalDateTime updatedAt;

    @Schema(description = "Username who created this record", example = "admin")
    private String createdBy;

    @Schema(description = "Username who last updated this record", example = "john.doe@company.com")
    private String updatedBy;

    @Schema(description = "Optimistic lock version", example = "5")
    private Long version;

    /**
     * Computes a human-readable tenure string based on hireDate and today (or
     * terminationDate).
     */
    public String getTenure() {
        if (hireDate == null)
            return null;
        LocalDate end = (terminationDate != null) ? terminationDate : LocalDate.now();
        Period period = Period.between(hireDate, end);
        if (period.getYears() > 0 && period.getMonths() > 0) {
            return period.getYears() + " year" + (period.getYears() > 1 ? "s" : "") + ", "
                    + period.getMonths() + " month" + (period.getMonths() > 1 ? "s" : "");
        } else if (period.getYears() > 0) {
            return period.getYears() + " year" + (period.getYears() > 1 ? "s" : "");
        } else {
            return period.getMonths() + " month" + (period.getMonths() > 1 ? "s" : "");
        }
    }

    // ==================== Nested Summary Classes ====================

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "Department summary within an employee response")
    public static class DepartmentSummary {
        @Schema(description = "Department ID", example = "1")
        private Long id;

        @Schema(description = "Department code", example = "DEPT-001")
        private String departmentCode;

        @Schema(description = "Department name", example = "Warehouse Operations")
        private String name;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "Manager summary within an employee response")
    public static class ManagerSummary {
        @Schema(description = "Employee ID", example = "5")
        private Long id;

        @Schema(description = "Employee code", example = "EMP-005")
        private String employeeCode;

        @Schema(description = "First name", example = "Sarah")
        private String firstName;

        @Schema(description = "Last name", example = "Director")
        private String lastName;

        @Schema(description = "Job title", example = "Operations Director")
        private String jobTitle;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "Subordinate summary within an employee response")
    public static class SubordinateSummary {
        @Schema(description = "Employee ID", example = "11")
        private Long id;

        @Schema(description = "Employee code", example = "EMP-011")
        private String employeeCode;

        @Schema(description = "First name", example = "Jane")
        private String firstName;

        @Schema(description = "Last name", example = "Staff")
        private String lastName;

        @Schema(description = "Job title", example = "Warehouse Staff")
        private String jobTitle;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "User account summary linked to an employee")
    public static class UserSummary {
        @Schema(description = "User ID", example = "2")
        private Long id;

        @Schema(description = "Username", example = "john_manager")
        private String username;

        @Schema(description = "Role name", example = "MANAGER")
        private String role;
    }
}