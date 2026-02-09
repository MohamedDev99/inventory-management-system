package com.moeware.ims.entity.customer;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import io.swagger.v3.oas.annotations.media.Schema;

import com.moeware.ims.entity.VersionedEntity;
import com.moeware.ims.entity.User;
import com.moeware.ims.entity.customer.Department;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "employees", indexes = {
        @Index(name = "idx_employees_code", columnList = "employee_code", unique = true),
        @Index(name = "idx_employees_email", columnList = "email", unique = true),
        @Index(name = "idx_employees_department", columnList = "department_id"),
        @Index(name = "idx_employees_manager", columnList = "manager_id"),
        @Index(name = "idx_employees_user", columnList = "user_id"),
        @Index(name = "idx_employees_name", columnList = "last_name, first_name"),
        @Index(name = "idx_employees_active", columnList = "id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Employee record with job information, organizational hierarchy, and system access")
public class Employee extends VersionedEntity {

    @Schema(description = "Unique identifier", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "Unique employee code", example = "EMP-001", required = true, maxLength = 50)
    @NotBlank(message = "Employee code is required")
    @Size(max = 50, message = "Employee code must not exceed 50 characters")
    @Column(name = "employee_code", nullable = false, unique = true, length = 50)
    private String employeeCode;

    @Schema(description = "Employee first name", example = "John", required = true, maxLength = 100)
    @NotBlank(message = "First name is required")
    @Size(max = 100, message = "First name must not exceed 100 characters")
    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Schema(description = "Employee last name", example = "Doe", required = true, maxLength = 100)
    @NotBlank(message = "Last name is required")
    @Size(max = 100, message = "Last name must not exceed 100 characters")
    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Schema(description = "Work email address", example = "john.doe@company.com", required = true, maxLength = 255, format = "email")
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Schema(description = "Contact phone number", example = "+1-555-1001", maxLength = 50)
    @Size(max = 50, message = "Phone must not exceed 50 characters")
    @Column(length = 50)
    private String phone;

    @Schema(description = "Job title or position", example = "Warehouse Manager", maxLength = 100)
    @Size(max = 100, message = "Job title must not exceed 100 characters")
    @Column(name = "job_title", length = 100)
    private String jobTitle;

    @Schema(description = "Department assignment", implementation = Department.class)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @Schema(description = "Direct manager", implementation = Employee.class)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private Employee manager;

    @Schema(description = "Direct reports (subordinates)", accessMode = Schema.AccessMode.READ_ONLY)
    @OneToMany(mappedBy = "manager", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<Employee> subordinates = new HashSet<>();

    @Schema(description = "Date of hire", example = "2023-01-15", required = true, format = "date")
    @NotNull(message = "Hire date is required")
    @Column(name = "hire_date", nullable = false)
    private LocalDate hireDate;

    @Schema(description = "Date of termination (if applicable)", example = "2025-12-31", format = "date")
    @Column(name = "termination_date")
    private LocalDate terminationDate;

    @Schema(description = "Annual salary", example = "75000.00", minimum = "0")
    @Column(precision = 12, scale = 2)
    private BigDecimal salary;

    @Schema(description = "Associated system user account", implementation = User.class)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Schema(description = "Whether the employee is active", example = "true", required = true)
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    // Helper methods
    public String getFullName() {
        return firstName + " " + lastName;
    }

    public boolean hasSystemAccess() {
        return user != null;
    }

    public boolean isManager() {
        return subordinates != null && !subordinates.isEmpty();
    }

    public int getSubordinateCount() {
        return subordinates != null ? subordinates.size() : 0;
    }

    public boolean isTerminated() {
        return terminationDate != null && !terminationDate.isAfter(LocalDate.now());
    }
}