package com.moeware.ims.controller.staff;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.moeware.ims.dto.ApiResponseWpp;
import com.moeware.ims.dto.staff.employee.AssignDepartmentDTO;
import com.moeware.ims.dto.staff.employee.AssignManagerDTO;
import com.moeware.ims.dto.staff.employee.EmployeePatchDTO;
import com.moeware.ims.dto.staff.employee.EmployeeRequestDTO;
import com.moeware.ims.dto.staff.employee.EmployeeResponseDTO;
import com.moeware.ims.dto.staff.employee.LinkUserDTO;
import com.moeware.ims.service.staff.EmployeeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST controller for Employee management endpoints
 *
 * @author MoeWare Team
 */
@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Employee Management", description = "APIs for managing employees, hierarchy, department assignments, and system user linking")
@SecurityRequirement(name = "bearerAuth")
public class EmployeeController {

    private final EmployeeService employeeService;

    // ==================== GET Endpoints ====================

    @Operation(summary = "List all employees", description = "Returns a paginated list of employees with optional filters. Accessible by ADMIN, MANAGER, and WAREHOUSE_STAFF.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Employees retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema(hidden = true)))
    })
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAREHOUSE_STAFF')")
    public ResponseEntity<ApiResponseWpp<Page<EmployeeResponseDTO>>> getAllEmployees(
            @Parameter(description = "Search by name, code, or email") @RequestParam(required = false) String search,
            @Parameter(description = "Filter by department ID") @RequestParam(required = false) Long departmentId,
            @Parameter(description = "Filter by manager ID") @RequestParam(required = false) Long managerId,
            @Parameter(description = "Filter by active status") @RequestParam(required = false) Boolean isActive,
            @Parameter(description = "Page number (0-based)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "20") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field", example = "lastName") @RequestParam(defaultValue = "lastName") String sort,
            @Parameter(description = "Sort direction (asc/desc)", example = "asc") @RequestParam(defaultValue = "asc") String direction) {
        log.debug("GET /api/employees - search={}, departmentId={}, managerId={}, isActive={}", search, departmentId,
                managerId, isActive);
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        Page<EmployeeResponseDTO> employees = employeeService.getAllEmployees(search, departmentId, managerId, isActive,
                pageable);
        return ResponseEntity.ok(ApiResponseWpp.success(employees, "Employees retrieved successfully"));
    }

    @Operation(summary = "Get employee by ID", description = "Returns full details of a specific employee including manager, direct reports, department, and linked user.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Employee found"),
            @ApiResponse(responseCode = "404", description = "Employee not found", content = @Content(schema = @Schema(hidden = true)))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAREHOUSE_STAFF')")
    public ResponseEntity<ApiResponseWpp<EmployeeResponseDTO>> getEmployeeById(
            @Parameter(description = "Employee ID", required = true, example = "10") @PathVariable Long id) {
        log.debug("GET /api/employees/{}", id);
        return ResponseEntity.ok(ApiResponseWpp.success(employeeService.getEmployeeById(id)));
    }

    @Operation(summary = "Get direct reports of an employee", description = "Returns a paginated list of employees who directly report to the specified employee (manager).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Subordinates retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Employee not found", content = @Content(schema = @Schema(hidden = true)))
    })
    @GetMapping("/{id}/subordinates")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAREHOUSE_STAFF')")
    public ResponseEntity<ApiResponseWpp<Page<EmployeeResponseDTO>>> getSubordinates(
            @Parameter(description = "Manager employee ID", required = true, example = "10") @PathVariable Long id,
            @Parameter(description = "Page number (0-based)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "20") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field", example = "lastName") @RequestParam(defaultValue = "lastName") String sort,
            @Parameter(description = "Sort direction (asc/desc)", example = "asc") @RequestParam(defaultValue = "asc") String direction) {
        log.debug("GET /api/employees/{}/subordinates", id);
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        Page<EmployeeResponseDTO> subordinates = employeeService.getSubordinates(id, pageable);
        return ResponseEntity.ok(ApiResponseWpp.success(subordinates, "Subordinates retrieved successfully"));
    }

    // ==================== POST Endpoints ====================

    @Operation(summary = "Create a new employee", description = "Creates a new employee record. Only ADMIN and MANAGER can create employees.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Employee created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "409", description = "Employee code or email already exists", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "Forbidden — ADMIN or MANAGER role required", content = @Content(schema = @Schema(hidden = true)))
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponseWpp<EmployeeResponseDTO>> createEmployee(
            @Valid @RequestBody EmployeeRequestDTO request) {
        log.info("POST /api/employees - code={}", request.getEmployeeCode());
        EmployeeResponseDTO created = employeeService.createEmployee(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseWpp.success(created, "Employee created successfully"));
    }

    @Operation(summary = "Link employee to a system user account", description = "Links an employee record to an existing system user account, granting system access. Only ADMIN can perform this action.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Employee linked to user successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "Employee or user not found", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "409", description = "User is already linked to another employee", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "Forbidden — ADMIN role required", content = @Content(schema = @Schema(hidden = true)))
    })
    @PostMapping("/{id}/link-user")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseWpp<EmployeeResponseDTO>> linkUser(
            @Parameter(description = "Employee ID", required = true, example = "10") @PathVariable Long id,
            @Valid @RequestBody LinkUserDTO request) {
        log.info("POST /api/employees/{}/link-user - userId={}", id, request.getUserId());
        return ResponseEntity.ok(
                ApiResponseWpp.success(employeeService.linkUser(id, request), "Employee linked to user successfully"));
    }

    // ==================== PUT Endpoints ====================

    @Operation(summary = "Update an employee (full update)", description = "Fully replaces all fields of an existing employee. Only ADMIN and MANAGER can update employees.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Employee updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "Employee not found", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "409", description = "Employee code or email already in use", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "Forbidden — ADMIN or MANAGER role required", content = @Content(schema = @Schema(hidden = true)))
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponseWpp<EmployeeResponseDTO>> updateEmployee(
            @Parameter(description = "Employee ID", required = true, example = "10") @PathVariable Long id,
            @Valid @RequestBody EmployeeRequestDTO request) {
        log.info("PUT /api/employees/{}", id);
        return ResponseEntity.ok(
                ApiResponseWpp.success(employeeService.updateEmployee(id, request), "Employee updated successfully"));
    }

    // ==================== PATCH Endpoints ====================

    @Operation(summary = "Partially update an employee", description = "Updates only the provided fields of an existing employee. Only ADMIN and MANAGER can patch employees.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Employee patched successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "Employee not found", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "Forbidden — ADMIN or MANAGER role required", content = @Content(schema = @Schema(hidden = true)))
    })
    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponseWpp<EmployeeResponseDTO>> patchEmployee(
            @Parameter(description = "Employee ID", required = true, example = "10") @PathVariable Long id,
            @Valid @RequestBody EmployeePatchDTO request) {
        log.info("PATCH /api/employees/{}", id);
        return ResponseEntity.ok(
                ApiResponseWpp.success(employeeService.patchEmployee(id, request), "Employee updated successfully"));
    }

    @Operation(summary = "Assign employee to a department", description = "Assigns an employee to a specific department. Only ADMIN and MANAGER can perform this action.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Department assigned successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid department ID", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "Employee or department not found", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "Forbidden — ADMIN or MANAGER role required", content = @Content(schema = @Schema(hidden = true)))
    })
    @PatchMapping("/{id}/assign-department")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponseWpp<EmployeeResponseDTO>> assignDepartment(
            @Parameter(description = "Employee ID", required = true, example = "10") @PathVariable Long id,
            @Valid @RequestBody AssignDepartmentDTO request) {
        log.info("PATCH /api/employees/{}/assign-department - departmentId={}", id, request.getDepartmentId());
        return ResponseEntity.ok(ApiResponseWpp.success(employeeService.assignDepartment(id, request),
                "Department assigned successfully"));
    }

    @Operation(summary = "Assign a manager to an employee", description = "Sets the direct manager of an employee. Only ADMIN and MANAGER can perform this action.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Manager assigned successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid manager ID or self-assignment", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "Employee or manager not found", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "Forbidden — ADMIN or MANAGER role required", content = @Content(schema = @Schema(hidden = true)))
    })
    @PatchMapping("/{id}/assign-manager")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponseWpp<EmployeeResponseDTO>> assignManager(
            @Parameter(description = "Employee ID", required = true, example = "11") @PathVariable Long id,
            @Valid @RequestBody AssignManagerDTO request) {
        log.info("PATCH /api/employees/{}/assign-manager - managerId={}", id, request.getManagerId());
        return ResponseEntity.ok(
                ApiResponseWpp.success(employeeService.assignManager(id, request), "Manager assigned successfully"));
    }

    // ==================== DELETE Endpoints ====================

    @Operation(summary = "Deactivate an employee", description = "Soft-deletes an employee by setting isActive = false. The employee record and all its data are retained. Only ADMIN can deactivate employees.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Employee deactivated successfully"),
            @ApiResponse(responseCode = "404", description = "Employee not found", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "Forbidden — ADMIN role required", content = @Content(schema = @Schema(hidden = true)))
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseWpp<EmployeeResponseDTO>> deactivateEmployee(
            @Parameter(description = "Employee ID", required = true, example = "10") @PathVariable Long id) {
        log.info("DELETE /api/employees/{}", id);
        return ResponseEntity.ok(
                ApiResponseWpp.success(employeeService.deactivateEmployee(id), "Employee deactivated successfully"));
    }

    @Operation(summary = "Unlink employee from system user account", description = "Removes the link between an employee and their system user account, revoking system access. Only ADMIN can perform this action.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Employee unlinked from user successfully"),
            @ApiResponse(responseCode = "404", description = "Employee not found", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "Forbidden — ADMIN role required", content = @Content(schema = @Schema(hidden = true)))
    })
    @DeleteMapping("/{id}/unlink-user")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseWpp<EmployeeResponseDTO>> unlinkUser(
            @Parameter(description = "Employee ID", required = true, example = "10") @PathVariable Long id) {
        log.info("DELETE /api/employees/{}/unlink-user", id);
        return ResponseEntity
                .ok(ApiResponseWpp.success(employeeService.unlinkUser(id), "Employee unlinked from user successfully"));
    }
}