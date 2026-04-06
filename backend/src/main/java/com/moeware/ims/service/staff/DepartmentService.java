package com.moeware.ims.service.staff;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.moeware.ims.dto.staff.department.DepartmentRequestDTO;
import com.moeware.ims.dto.staff.department.DepartmentResponseDTO;
import com.moeware.ims.dto.staff.department.DepartmentStatsDTO;
import com.moeware.ims.dto.staff.employee.EmployeeResponseDTO;
import com.moeware.ims.entity.staff.Department;
import com.moeware.ims.entity.staff.Employee;
import com.moeware.ims.exception.staff.department.DepartmentAlreadyExistsException;
import com.moeware.ims.exception.staff.department.DepartmentAlreadyExistsException.ConflictField;
import com.moeware.ims.exception.staff.department.DepartmentNotFoundException;
import com.moeware.ims.exception.staff.department.InvalidDepartmentOperationException;
import com.moeware.ims.exception.staff.employee.EmployeeNotFoundException;
import com.moeware.ims.mapper.staff.department.DepartmentMapper;
import com.moeware.ims.mapper.staff.employee.EmployeeMapper;
import com.moeware.ims.repository.staff.DepartmentRepository;
import com.moeware.ims.repository.staff.EmployeeRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service for Department business logic and organizational structure management
 *
 * @author MoeWare Team
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;
    private final DepartmentMapper departmentMapper;
    private final EmployeeMapper employeeMapper;

    // ==================== Query Operations ====================

    /**
     * List departments with optional filters and pagination.
     */
    public Page<DepartmentResponseDTO> getAllDepartments(String search, Long parentId, Boolean isActive,
            Pageable pageable) {
        log.debug("Fetching departments with search={}, parentId={}, isActive={}", search, parentId, isActive);
        return departmentRepository.findAllWithFiltersPaged(search, parentId, isActive, pageable)
                .map(departmentMapper::toResponseDTO);
    }

    /**
     * Get a single department by ID with full details.
     */
    public DepartmentResponseDTO getDepartmentById(Long id) {
        log.debug("Fetching department with id={}", id);
        Department department = departmentRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new DepartmentNotFoundException(id));
        return departmentMapper.toResponseDTO(department);
    }

    /**
     * Get employees in a department (paginated).
     */
    public Page<EmployeeResponseDTO> getDepartmentEmployees(Long departmentId, Pageable pageable) {
        log.debug("Fetching employees for department id={}", departmentId);
        if (!departmentRepository.existsById(departmentId)) {
            throw new DepartmentNotFoundException(departmentId);
        }
        return departmentRepository.findEmployeesByDepartmentId(departmentId, pageable)
                .map(employeeMapper::toSummaryDTO);
    }

    /**
     * Get statistics for a specific department.
     */
    public DepartmentStatsDTO getDepartmentStats(Long id) {
        log.debug("Fetching stats for department id={}", id);
        Department department = departmentRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new DepartmentNotFoundException(id));
        return departmentMapper.toStatsDTO(department);
    }

    // ==================== Mutation Operations ====================

    /**
     * Create a new department.
     */
    @Transactional
    public DepartmentResponseDTO createDepartment(DepartmentRequestDTO request) {
        log.info("Creating department with code={}", request.getDepartmentCode());

        validateUniqueness(request.getDepartmentCode(), request.getName(), null);

        Department department = Department.builder()
                .departmentCode(request.getDepartmentCode())
                .name(request.getName())
                .description(request.getDescription())
                .costCenter(request.getCostCenter())
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .build();

        if (request.getManagerId() != null) {
            Employee manager = employeeRepository.findById(request.getManagerId())
                    .orElseThrow(() -> new EmployeeNotFoundException(request.getManagerId()));
            department.setManager(manager);
        }

        if (request.getParentDepartmentId() != null) {
            Department parent = departmentRepository.findById(request.getParentDepartmentId())
                    .orElseThrow(() -> new DepartmentNotFoundException(request.getParentDepartmentId()));
            department.setParentDepartment(parent);
        }

        Department saved = departmentRepository.save(department);
        log.info("Created department id={}", saved.getId());
        return departmentMapper.toResponseDTO(saved);
    }

    /**
     * Update an existing department (full update).
     */
    @Transactional
    public DepartmentResponseDTO updateDepartment(Long id, DepartmentRequestDTO request) {
        log.info("Updating department id={}", id);

        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new DepartmentNotFoundException(id));

        validateUniqueness(request.getDepartmentCode(), request.getName(), id);

        department.setDepartmentCode(request.getDepartmentCode());
        department.setName(request.getName());
        department.setDescription(request.getDescription());
        department.setCostCenter(request.getCostCenter());
        department.setIsActive(request.getIsActive() != null ? request.getIsActive() : department.getIsActive());

        if (request.getManagerId() != null) {
            Employee manager = employeeRepository.findById(request.getManagerId())
                    .orElseThrow(() -> new EmployeeNotFoundException(request.getManagerId()));
            department.setManager(manager);
        } else {
            department.setManager(null);
        }

        if (request.getParentDepartmentId() != null) {
            // Prevent circular hierarchy
            if (request.getParentDepartmentId().equals(id)) {
                throw new InvalidDepartmentOperationException("A department cannot be its own parent");
            }
            Department parent = departmentRepository.findById(request.getParentDepartmentId())
                    .orElseThrow(() -> new DepartmentNotFoundException(request.getParentDepartmentId()));
            department.setParentDepartment(parent);
        } else {
            department.setParentDepartment(null);
        }

        Department updated = departmentRepository.save(department);
        log.info("Updated department id={}", updated.getId());
        return departmentMapper.toResponseDTO(updated);
    }

    /**
     * Soft delete a department by setting isActive = false.
     */
    @Transactional
    public DepartmentResponseDTO deleteDepartment(Long id) {
        log.info("Soft-deleting department id={}", id);

        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new DepartmentNotFoundException(id));

        department.setIsActive(false);
        Department saved = departmentRepository.save(department);
        log.info("Department id={} deactivated", id);
        return departmentMapper.toResponseDTO(saved);
    }

    // ==================== Private Helpers ====================

    private void validateUniqueness(String code, String name, Long excludeId) {
        if (excludeId == null) {
            if (departmentRepository.existsByDepartmentCode(code)) {
                throw new DepartmentAlreadyExistsException(ConflictField.DEPARTMENT_CODE, code);
            }
            if (departmentRepository.existsByName(name)) {
                throw new DepartmentAlreadyExistsException(ConflictField.NAME, name);
            }
        } else {
            if (departmentRepository.existsByDepartmentCodeAndIdNot(code, excludeId)) {
                throw new DepartmentAlreadyExistsException(ConflictField.DEPARTMENT_CODE, code);
            }
            if (departmentRepository.existsByNameAndIdNot(name, excludeId)) {
                throw new DepartmentAlreadyExistsException(ConflictField.NAME, name);
            }
        }
    }
}