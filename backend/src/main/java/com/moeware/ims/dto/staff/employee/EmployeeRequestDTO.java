package com.moeware.ims.dto.staff.employee;

import java.math.BigDecimal;
import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating or fully updating an Employee
 *
 * @author MoeWare Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request payload for creating or updating an employee")
public class EmployeeRequestDTO {

    @Schema(description = "Unique employee code", example = "EMP-001", requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 50)
    @NotBlank(message = "Employee code is required")
    @Size(max = 50, message = "Employee code must not exceed 50 characters")
    private String employeeCode;

    @Schema(description = "Employee first name", example = "John", requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 100)
    @NotBlank(message = "First name is required")
    @Size(max = 100, message = "First name must not exceed 100 characters")
    private String firstName;

    @Schema(description = "Employee last name", example = "Doe", requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 100)
    @NotBlank(message = "Last name is required")
    @Size(max = 100, message = "Last name must not exceed 100 characters")
    private String lastName;

    @Schema(description = "Work email address", example = "john.doe@company.com", requiredMode = Schema.RequiredMode.REQUIRED, format = "email")
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    private String email;

    @Schema(description = "Contact phone number", example = "+1-555-1001", maxLength = 50)
    @Size(max = 50, message = "Phone must not exceed 50 characters")
    private String phone;

    @Schema(description = "Job title or position", example = "Warehouse Manager", maxLength = 100)
    @Size(max = 100, message = "Job title must not exceed 100 characters")
    private String jobTitle;

    @Schema(description = "ID of the department to assign this employee to", example = "1")
    private Long departmentId;

    @Schema(description = "ID of the direct manager employee", example = "10")
    private Long managerId;

    @Schema(description = "Date of hire", example = "2023-01-15", requiredMode = Schema.RequiredMode.REQUIRED, format = "date")
    @NotNull(message = "Hire date is required")
    private LocalDate hireDate;

    @Schema(description = "Date of termination (if applicable)", example = "2025-12-31", format = "date")
    private LocalDate terminationDate;

    @Schema(description = "Annual salary", example = "75000.00", minimum = "0")
    @DecimalMin(value = "0.0", inclusive = true, message = "Salary must be non-negative")
    private BigDecimal salary;

    @Schema(description = "Whether the employee is active", example = "true")
    @Builder.Default
    private Boolean isActive = true;
}