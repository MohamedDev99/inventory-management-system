package com.moeware.ims.service.staff;

import com.moeware.ims.dto.staff.employee.AssignDepartmentDTO;
import com.moeware.ims.dto.staff.employee.AssignManagerDTO;
import com.moeware.ims.dto.staff.employee.EmployeePatchDTO;
import com.moeware.ims.dto.staff.employee.EmployeeRequestDTO;
import com.moeware.ims.dto.staff.employee.EmployeeResponseDTO;
import com.moeware.ims.dto.staff.employee.LinkUserDTO;
import com.moeware.ims.entity.User;
import com.moeware.ims.entity.staff.Department;
import com.moeware.ims.entity.staff.Employee;
import com.moeware.ims.exception.ResourceNotFoundException;
import com.moeware.ims.exception.staff.department.DepartmentNotFoundException;
import com.moeware.ims.exception.staff.employee.EmployeeAlreadyExistsException;
import com.moeware.ims.exception.staff.employee.EmployeeNotFoundException;
import com.moeware.ims.mapper.staff.employee.EmployeeMapper;
import com.moeware.ims.repository.staff.DepartmentRepository;
import com.moeware.ims.repository.staff.EmployeeRepository;
import com.moeware.ims.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for Employee business logic and hierarchy management
 *
 * @author MoeWare Team
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final EmployeeMapper employeeMapper;
    private final UserRepository userRepository;

    // ==================== Query Operations ====================

    /**
     * List all employees with optional filters and pagination.
     */
    public Page<EmployeeResponseDTO> getAllEmployees(
            String search, Long departmentId, Long managerId, Boolean isActive, Pageable pageable) {
        log.debug("Fetching employees: search={}, departmentId={}, managerId={}, isActive={}", search, departmentId,
                managerId, isActive);
        return employeeRepository.findAllWithFilters(search, departmentId, managerId, isActive, pageable)
                .map(employeeMapper::toSummaryDTO);
    }

    /**
     * Get a single employee by ID with full details.
     */
    public EmployeeResponseDTO getEmployeeById(Long id) {
        log.debug("Fetching employee id={}", id);
        Employee employee = employeeRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));
        return employeeMapper.toResponseDTO(employee);
    }

    /**
     * Get direct subordinates of an employee (paginated).
     */
    public Page<EmployeeResponseDTO> getSubordinates(Long managerId, Pageable pageable) {
        log.debug("Fetching subordinates for manager id={}", managerId);
        if (!employeeRepository.existsById(managerId)) {
            throw new EmployeeNotFoundException(managerId);
        }
        return employeeRepository.findSubordinatesByManagerId(managerId, pageable)
                .map(employeeMapper::toSummaryDTO);
    }

    // ==================== Mutation Operations ====================

    /**
     * Create a new employee.
     */
    @Transactional
    public EmployeeResponseDTO createEmployee(EmployeeRequestDTO request) {
        log.info("Creating employee with code={}", request.getEmployeeCode());

        validateUniqueFields(request.getEmployeeCode(), request.getEmail(), null);

        Employee employee = Employee.builder()
                .employeeCode(request.getEmployeeCode())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .jobTitle(request.getJobTitle())
                .hireDate(request.getHireDate())
                .terminationDate(request.getTerminationDate())
                .salary(request.getSalary())
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .build();

        if (request.getDepartmentId() != null) {
            Department department = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new DepartmentNotFoundException(request.getDepartmentId()));
            employee.setDepartment(department);
        }

        if (request.getManagerId() != null) {
            Employee manager = employeeRepository.findById(request.getManagerId())
                    .orElseThrow(() -> new EmployeeNotFoundException(request.getManagerId()));
            employee.setManager(manager);
        }

        Employee saved = employeeRepository.save(employee);
        log.info("Created employee id={}", saved.getId());
        return employeeMapper.toResponseDTO(saved);
    }

    /**
     * Full update of an employee.
     */
    @Transactional
    public EmployeeResponseDTO updateEmployee(Long id, EmployeeRequestDTO request) {
        log.info("Updating employee id={}", id);

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));

        validateUniqueFields(request.getEmployeeCode(), request.getEmail(), id);

        employee.setEmployeeCode(request.getEmployeeCode());
        employee.setFirstName(request.getFirstName());
        employee.setLastName(request.getLastName());
        employee.setEmail(request.getEmail());
        employee.setPhone(request.getPhone());
        employee.setJobTitle(request.getJobTitle());
        employee.setHireDate(request.getHireDate());
        employee.setTerminationDate(request.getTerminationDate());
        employee.setSalary(request.getSalary());
        employee.setIsActive(request.getIsActive() != null ? request.getIsActive() : employee.getIsActive());

        if (request.getDepartmentId() != null) {
            Department department = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new DepartmentNotFoundException(request.getDepartmentId()));
            employee.setDepartment(department);
        } else {
            employee.setDepartment(null);
        }

        if (request.getManagerId() != null) {
            if (request.getManagerId().equals(id)) {
                throw new IllegalArgumentException("An employee cannot be their own manager");
            }
            Employee manager = employeeRepository.findById(request.getManagerId())
                    .orElseThrow(() -> new EmployeeNotFoundException(request.getManagerId()));
            employee.setManager(manager);
        } else {
            employee.setManager(null);
        }

        Employee updated = employeeRepository.save(employee);
        log.info("Updated employee id={}", updated.getId());
        return employeeMapper.toResponseDTO(updated);
    }

    /**
     * Partial update of an employee (PATCH).
     */
    @Transactional
    public EmployeeResponseDTO patchEmployee(Long id, EmployeePatchDTO request) {
        log.info("Patching employee id={}", id);

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));

        if (request.getEmail() != null && !request.getEmail().equalsIgnoreCase(employee.getEmail())) {
            if (employeeRepository.existsByEmailAndIdNot(request.getEmail(), id)) {
                throw new EmployeeAlreadyExistsException("email", request.getEmail());
            }
            employee.setEmail(request.getEmail());
        }
        if (request.getFirstName() != null)
            employee.setFirstName(request.getFirstName());
        if (request.getLastName() != null)
            employee.setLastName(request.getLastName());
        if (request.getPhone() != null)
            employee.setPhone(request.getPhone());
        if (request.getJobTitle() != null)
            employee.setJobTitle(request.getJobTitle());
        if (request.getSalary() != null)
            employee.setSalary(request.getSalary());
        if (request.getTerminationDate() != null)
            employee.setTerminationDate(request.getTerminationDate());
        if (request.getIsActive() != null)
            employee.setIsActive(request.getIsActive());

        Employee updated = employeeRepository.save(employee);
        log.info("Patched employee id={}", updated.getId());
        return employeeMapper.toResponseDTO(updated);
    }

    /**
     * Soft delete (deactivate) an employee.
     */
    @Transactional
    public EmployeeResponseDTO deactivateEmployee(Long id) {
        log.info("Deactivating employee id={}", id);

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));

        employee.setIsActive(false);
        Employee saved = employeeRepository.save(employee);
        log.info("Employee id={} deactivated", id);
        return employeeMapper.toResponseDTO(saved);
    }

    /**
     * Assign employee to a department.
     */
    @Transactional
    public EmployeeResponseDTO assignDepartment(Long employeeId, AssignDepartmentDTO request) {
        log.info("Assigning employee id={} to department id={}", employeeId, request.getDepartmentId());

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException(employeeId));

        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new DepartmentNotFoundException(request.getDepartmentId()));

        employee.setDepartment(department);
        return employeeMapper.toResponseDTO(employeeRepository.save(employee));
    }

    /**
     * Assign a manager to an employee.
     */
    @Transactional
    public EmployeeResponseDTO assignManager(Long employeeId, AssignManagerDTO request) {
        log.info("Assigning manager id={} to employee id={}", request.getManagerId(), employeeId);

        if (employeeId.equals(request.getManagerId())) {
            throw new IllegalArgumentException("An employee cannot be their own manager");
        }

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException(employeeId));

        Employee manager = employeeRepository.findById(request.getManagerId())
                .orElseThrow(() -> new EmployeeNotFoundException(request.getManagerId()));

        employee.setManager(manager);
        return employeeMapper.toResponseDTO(employeeRepository.save(employee));
    }

    /**
     * Link employee to a system user account.
     */
    @Transactional
    public EmployeeResponseDTO linkUser(Long employeeId, LinkUserDTO request) {
        log.info("Linking employee id={} to user id={}", employeeId, request.getUserId());

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException(employeeId));

        // Ensure user exists
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getUserId()));

        // Ensure the user is not already linked to another employee
        if (employeeRepository.existsByUserId(request.getUserId())) {
            throw new EmployeeAlreadyExistsException(
                    "An employee is already linked to user id: " + request.getUserId());
        }

        employee.setUser(user);
        return employeeMapper.toResponseDTO(employeeRepository.save(employee));
    }

    /**
     * Unlink employee from system user account.
     */
    @Transactional
    public EmployeeResponseDTO unlinkUser(Long employeeId) {
        log.info("Unlinking user from employee id={}", employeeId);

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException(employeeId));

        employee.setUser(null);
        return employeeMapper.toResponseDTO(employeeRepository.save(employee));
    }

    // ==================== Private Helpers ====================

    private void validateUniqueFields(String code, String email, Long excludeId) {
        if (excludeId == null) {
            if (employeeRepository.existsByEmployeeCode(code)) {
                throw new EmployeeAlreadyExistsException("employee_code", code);
            }
            if (employeeRepository.existsByEmail(email)) {
                throw new EmployeeAlreadyExistsException("email", email);
            }
        } else {
            if (employeeRepository.existsByEmployeeCodeAndIdNot(code, excludeId)) {
                throw new EmployeeAlreadyExistsException("employee_code", code);
            }
            if (employeeRepository.existsByEmailAndIdNot(email, excludeId)) {
                throw new EmployeeAlreadyExistsException("email", email);
            }
        }
    }
}