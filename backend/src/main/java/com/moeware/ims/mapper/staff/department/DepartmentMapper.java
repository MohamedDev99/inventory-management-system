package com.moeware.ims.mapper.staff.department;

import com.moeware.ims.dto.staff.department.DepartmentResponseDTO;
import com.moeware.ims.dto.staff.department.DepartmentStatsDTO;
import com.moeware.ims.entity.staff.Department;
import com.moeware.ims.entity.staff.Employee;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between Department entity and DTOs
 *
 * @author MoeWare Team
 */
@Component
public class DepartmentMapper {

    /**
     * Maps a Department entity to a DepartmentResponseDTO.
     */
    public DepartmentResponseDTO toResponseDTO(Department department) {
        if (department == null)
            return null;

        return DepartmentResponseDTO.builder()
                .id(department.getId())
                .departmentCode(department.getDepartmentCode())
                .name(department.getName())
                .description(department.getDescription())
                .manager(mapManagerSummary(department.getManager()))
                .parentDepartment(mapParentSummary(department.getParentDepartment()))
                .childDepartmentCount(
                        department.getChildDepartments() != null ? department.getChildDepartments().size() : 0)
                .costCenter(department.getCostCenter())
                .employeeCount(department.getEmployees() != null ? department.getEmployees().size() : 0)
                .isActive(department.getIsActive())
                .rootDepartment(department.isRootDepartment())
                .createdAt(department.getCreatedAt())
                .updatedAt(department.getUpdatedAt())
                .createdBy(department.getCreatedBy())
                .updatedBy(department.getUpdatedBy())
                .build();
    }

    /**
     * Maps a Department entity to a DepartmentStatsDTO.
     */
    public DepartmentStatsDTO toStatsDTO(Department department) {
        if (department == null)
            return null;

        long activeCount = department.getEmployees().stream()
                .filter(e -> Boolean.TRUE.equals(e.getIsActive()))
                .count();

        long withAccessCount = department.getEmployees().stream()
                .filter(Employee::hasSystemAccess)
                .count();

        return DepartmentStatsDTO.builder()
                .departmentId(department.getId())
                .departmentName(department.getName())
                .totalEmployees(department.getEmployees().size())
                .activeEmployees((int) activeCount)
                .inactiveEmployees((int) (department.getEmployees().size() - activeCount))
                .employeesWithSystemAccess((int) withAccessCount)
                .childDepartmentCount(department.getChildDepartments().size())
                .hasManager(department.getManager() != null)
                .rootDepartment(department.isRootDepartment())
                .build();
    }

    // ==================== Private Helpers ====================

    private DepartmentResponseDTO.ManagerSummary mapManagerSummary(Employee manager) {
        if (manager == null)
            return null;
        return DepartmentResponseDTO.ManagerSummary.builder()
                .id(manager.getId())
                .employeeCode(manager.getEmployeeCode())
                .firstName(manager.getFirstName())
                .lastName(manager.getLastName())
                .jobTitle(manager.getJobTitle())
                .build();
    }

    private DepartmentResponseDTO.ParentDepartmentSummary mapParentSummary(Department parent) {
        if (parent == null)
            return null;
        return DepartmentResponseDTO.ParentDepartmentSummary.builder()
                .id(parent.getId())
                .departmentCode(parent.getDepartmentCode())
                .name(parent.getName())
                .build();
    }
}