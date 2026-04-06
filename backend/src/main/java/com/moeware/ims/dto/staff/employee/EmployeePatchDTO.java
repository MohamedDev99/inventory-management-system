package com.moeware.ims.dto.staff.employee;

import java.math.BigDecimal;
import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for partially updating an Employee (PATCH).
 * All fields are optional — only non-null fields will be applied.
 *
 * @author MoeWare Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request payload for partially updating an employee (only provided fields are updated)")
public class EmployeePatchDTO {

    @Schema(description = "Employee first name", example = "John", maxLength = 100)
    @Size(max = 100, message = "First name must not exceed 100 characters")
    @Pattern(regexp = "^(?!\\s*$).+", message = "Field must not be blank if provided")
    private String firstName;

    @Schema(description = "Employee last name", example = "Doe", maxLength = 100)
    @Size(max = 100, message = "Last name must not exceed 100 characters")
    @Pattern(regexp = "^(?!\\s*$).+", message = "Field must not be blank if provided")
    private String lastName;

    @Schema(description = "Work email address", example = "john.doe@company.com", format = "email")
    @Email(message = "Invalid email format")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    private String email;

    @Schema(description = "Contact phone number", example = "+1-555-1001", maxLength = 50)
    @Size(max = 50, message = "Phone must not exceed 50 characters")
    @Pattern(regexp = "^(?!\\s*$).+", message = "Field must not be blank if provided")
    private String phone;

    @Schema(description = "Job title or position", example = "Senior Warehouse Manager", maxLength = 100)
    @Size(max = 100, message = "Job title must not exceed 100 characters")
    @Pattern(regexp = "^(?!\\s*$).+", message = "Field must not be blank if provided")
    private String jobTitle;

    @Schema(description = "Annual salary", example = "80000.00", minimum = "0")
    @DecimalMin(value = "0.0", inclusive = true, message = "Salary must be non-negative")
    private BigDecimal salary;

    @Schema(description = "Date of termination (if applicable)", example = "2025-12-31", format = "date")
    private LocalDate terminationDate;

    @Schema(description = "Whether the employee is active", example = "true")
    private Boolean isActive;

    @AssertTrue(message = "At least one field must be provided for update")
    public boolean isAtLeastOneFieldPresent() {
        return firstName != null || lastName != null || email != null ||
                phone != null || jobTitle != null || salary != null ||
                terminationDate != null || isActive != null;
    }

}