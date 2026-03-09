package com.moeware.ims.mapper.staff.employee;

import com.moeware.ims.dto.staff.employee.EmployeeResponseDTO;
import com.moeware.ims.entity.staff.Department;
import com.moeware.ims.entity.staff.Employee;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Mapper for converting between Employee entity and DTOs
 *
 * @author MoeWare Team
 */
@Component
public class EmployeeMapper {

    /**
     * Maps an Employee entity to an EmployeeResponseDTO (includes direct reports).
     */
    public EmployeeResponseDTO toResponseDTO(Employee employee) {
        if (employee == null)
            return null;

        return EmployeeResponseDTO.builder()
                .id(employee.getId())
                .employeeCode(employee.getEmployeeCode())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .fullName(employee.getFullName())
                .email(employee.getEmail())
                .phone(employee.getPhone())
                .jobTitle(employee.getJobTitle())
                .department(mapDepartmentSummary(employee.getDepartment()))
                .manager(mapManagerSummary(employee.getManager()))
                .directReports(mapSubordinates(employee.getSubordinates()))
                .hireDate(employee.getHireDate())
                .terminationDate(employee.getTerminationDate())
                .salary(employee.getSalary())
                .isActive(employee.getIsActive())
                .terminated(employee.isTerminated())
                .hasSystemAccess(employee.hasSystemAccess())
                .user(mapUserSummary(employee))
                .createdAt(employee.getCreatedAt())
                .updatedAt(employee.getUpdatedAt())
                .createdBy(employee.getCreatedBy())
                .updatedBy(employee.getUpdatedBy())
                .version(employee.getVersion())
                .build();
    }

    /**
     * Maps an Employee entity to a lightweight summary DTO (no direct reports, no
     * audit fields).
     * Used for list endpoints to reduce payload size.
     */
    public EmployeeResponseDTO toSummaryDTO(Employee employee) {
        if (employee == null)
            return null;

        return EmployeeResponseDTO.builder()
                .id(employee.getId())
                .employeeCode(employee.getEmployeeCode())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .fullName(employee.getFullName())
                .email(employee.getEmail())
                .phone(employee.getPhone())
                .jobTitle(employee.getJobTitle())
                .department(mapDepartmentSummary(employee.getDepartment()))
                .manager(mapManagerSummary(employee.getManager()))
                .hireDate(employee.getHireDate())
                .terminationDate(employee.getTerminationDate())
                .isActive(employee.getIsActive())
                .terminated(employee.isTerminated())
                .hasSystemAccess(employee.hasSystemAccess())
                .user(mapUserSummary(employee))
                .createdAt(employee.getCreatedAt())
                .build();
    }

    // ==================== Private Helpers ====================

    private EmployeeResponseDTO.DepartmentSummary mapDepartmentSummary(Department dept) {
        if (dept == null)
            return null;
        return EmployeeResponseDTO.DepartmentSummary.builder()
                .id(dept.getId())
                .departmentCode(dept.getDepartmentCode())
                .name(dept.getName())
                .build();
    }

    private EmployeeResponseDTO.ManagerSummary mapManagerSummary(Employee manager) {
        if (manager == null)
            return null;
        return EmployeeResponseDTO.ManagerSummary.builder()
                .id(manager.getId())
                .employeeCode(manager.getEmployeeCode())
                .firstName(manager.getFirstName())
                .lastName(manager.getLastName())
                .jobTitle(manager.getJobTitle())
                .build();
    }

    private List<EmployeeResponseDTO.SubordinateSummary> mapSubordinates(Set<Employee> subordinates) {
        if (subordinates == null || subordinates.isEmpty())
            return Collections.emptyList();
        return subordinates.stream()
                .map(s -> EmployeeResponseDTO.SubordinateSummary.builder()
                        .id(s.getId())
                        .employeeCode(s.getEmployeeCode())
                        .firstName(s.getFirstName())
                        .lastName(s.getLastName())
                        .jobTitle(s.getJobTitle())
                        .build())
                .collect(Collectors.toList());
    }

    private EmployeeResponseDTO.UserSummary mapUserSummary(Employee employee) {
        if (employee.getUser() == null)
            return null;
        return EmployeeResponseDTO.UserSummary.builder()
                .id(employee.getUser().getId())
                .username(employee.getUser().getUsername())
                .role(employee.getUser().getRole() != null ? employee.getUser().getRole().getName() : null)
                .build();
    }
}