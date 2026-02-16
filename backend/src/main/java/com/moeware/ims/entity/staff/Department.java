package com.moeware.ims.entity.staff;

import java.util.HashSet;
import java.util.Set;

import com.moeware.ims.entity.AuditableEntity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "departments", indexes = {
        @Index(name = "idx_departments_code", columnList = "department_code", unique = true),
        @Index(name = "idx_departments_manager", columnList = "manager_id"),
        @Index(name = "idx_departments_parent", columnList = "parent_department_id"),
        @Index(name = "idx_departments_active", columnList = "id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Organizational department with hierarchical structure and employee management")
public class Department extends AuditableEntity {

    @Schema(description = "Unique identifier", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "Unique department code", example = "DEPT-001", requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 50)
    @NotBlank(message = "Department code is required")
    @Size(max = 50, message = "Department code must not exceed 50 characters")
    @Column(name = "department_code", nullable = false, unique = true, length = 50)
    private String departmentCode;

    @Schema(description = "Department name", example = "Warehouse Operations", requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 100)
    @NotBlank(message = "Department name is required")
    @Size(max = 100, message = "Department name must not exceed 100 characters")
    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Schema(description = "Department description", example = "Manages all warehouse activities and inventory operations")
    @Column(columnDefinition = "TEXT")
    private String description;

    @Schema(description = "Department manager employee", implementation = Employee.class)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private Employee manager;

    @Schema(description = "Parent department for hierarchical structure", implementation = Department.class)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_department_id")
    private Department parentDepartment;

    @Schema(description = "Child departments under this department", accessMode = Schema.AccessMode.READ_ONLY)
    @OneToMany(mappedBy = "parentDepartment", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<Department> childDepartments = new HashSet<>();

    @Schema(description = "Cost center code for accounting", example = "CC-WH-001", maxLength = 50)
    @Size(max = 50, message = "Cost center must not exceed 50 characters")
    @Column(name = "cost_center", length = 50)
    private String costCenter;

    @Schema(description = "Whether the department is active", example = "true", requiredMode = Schema.RequiredMode.REQUIRED)
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Schema(description = "Employees assigned to this department", accessMode = Schema.AccessMode.READ_ONLY)
    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<Employee> employees = new HashSet<>();

    // Helper methods
    public void addEmployee(Employee employee) {
        employees.add(employee);
        employee.setDepartment(this);
    }

    public void removeEmployee(Employee employee) {
        employees.remove(employee);
        employee.setDepartment(null);
    }

    public boolean isRootDepartment() {
        return parentDepartment == null;
    }

    public int getEmployeeCount() {
        return employees.size();
    }
}