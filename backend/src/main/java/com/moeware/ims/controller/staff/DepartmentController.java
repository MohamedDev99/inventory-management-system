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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.moeware.ims.dto.ApiResponseWpp;
import com.moeware.ims.dto.staff.department.DepartmentRequestDTO;
import com.moeware.ims.dto.staff.department.DepartmentResponseDTO;
import com.moeware.ims.dto.staff.department.DepartmentStatsDTO;
import com.moeware.ims.dto.staff.employee.EmployeeResponseDTO;
import com.moeware.ims.service.staff.DepartmentService;

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
 * REST controller for Department management endpoints
 *
 * @author MoeWare Team
 */
@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Department Management", description = "APIs for managing organizational departments and hierarchy")
@SecurityRequirement(name = "bearerAuth")
public class DepartmentController {

    private final DepartmentService departmentService;

    // ==================== GET Endpoints ====================

    @Operation(summary = "List all departments", description = "Returns a paginated list of departments with optional search and filter parameters. Accessible by all authenticated users.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Departments retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema(hidden = true)))
    })
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseWpp<Page<DepartmentResponseDTO>>> getAllDepartments(
            @Parameter(description = "Search term (name or code)") @RequestParam(required = false) String search,
            @Parameter(description = "Filter by parent department ID") @RequestParam(required = false) Long parentId,
            @Parameter(description = "Filter by active status") @RequestParam(required = false) Boolean isActive,
            @Parameter(description = "Page number (0-based)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "20") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field", example = "name") @RequestParam(defaultValue = "name") String sort,
            @Parameter(description = "Sort direction (asc/desc)", example = "asc") @RequestParam(defaultValue = "asc") String direction) {
        log.debug("GET /api/departments - search={}, parentId={}, isActive={}, page={}, size={}", search, parentId,
                isActive, page, size);
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        Page<DepartmentResponseDTO> departments = departmentService.getAllDepartments(search, parentId, isActive,
                pageable);
        return ResponseEntity.ok(ApiResponseWpp.success(departments, "Departments retrieved successfully"));
    }

    @Operation(summary = "Get department by ID", description = "Returns full details of a specific department including manager, parent, and child department counts.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Department found"),
            @ApiResponse(responseCode = "404", description = "Department not found", content = @Content(schema = @Schema(hidden = true)))
    })
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseWpp<DepartmentResponseDTO>> getDepartmentById(
            @Parameter(description = "Department ID", required = true, example = "1") @PathVariable Long id) {
        log.debug("GET /api/departments/{}", id);
        return ResponseEntity.ok(ApiResponseWpp.success(departmentService.getDepartmentById(id)));
    }

    @Operation(summary = "Get employees in a department", description = "Returns a paginated list of employees assigned to the specified department.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Employees retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Department not found", content = @Content(schema = @Schema(hidden = true)))
    })
    @GetMapping("/{id}/employees")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseWpp<Page<EmployeeResponseDTO>>> getDepartmentEmployees(
            @Parameter(description = "Department ID", required = true, example = "1") @PathVariable Long id,
            @Parameter(description = "Page number (0-based)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "20") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field", example = "lastName") @RequestParam(defaultValue = "lastName") String sort,
            @Parameter(description = "Sort direction (asc/desc)", example = "asc") @RequestParam(defaultValue = "asc") String direction) {
        log.debug("GET /api/departments/{}/employees", id);
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        Page<EmployeeResponseDTO> employees = departmentService.getDepartmentEmployees(id, pageable);
        return ResponseEntity.ok(ApiResponseWpp.success(employees, "Department employees retrieved successfully"));
    }

    @Operation(summary = "Get department statistics", description = "Returns aggregated statistics for a department: total employees, active employees, child departments, etc.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Department not found", content = @Content(schema = @Schema(hidden = true)))
    })
    @GetMapping("/{id}/stats")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseWpp<DepartmentStatsDTO>> getDepartmentStats(
            @Parameter(description = "Department ID", required = true, example = "1") @PathVariable Long id) {
        log.debug("GET /api/departments/{}/stats", id);
        return ResponseEntity.ok(ApiResponseWpp.success(departmentService.getDepartmentStats(id),
                "Department statistics retrieved successfully"));
    }

    // ==================== POST Endpoints ====================

    @Operation(summary = "Create a new department", description = "Creates a new organizational department. Only ADMIN users can create departments.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Department created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "409", description = "Department code or name already exists", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "Forbidden — ADMIN role required", content = @Content(schema = @Schema(hidden = true)))
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseWpp<DepartmentResponseDTO>> createDepartment(
            @Valid @RequestBody DepartmentRequestDTO request) {
        log.info("POST /api/departments - code={}", request.getDepartmentCode());
        DepartmentResponseDTO created = departmentService.createDepartment(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseWpp.success(created, "Department created successfully"));
    }

    // ==================== PUT Endpoints ====================

    @Operation(summary = "Update a department", description = "Fully updates an existing department. Only ADMIN users can update departments.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Department updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "Department not found", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "409", description = "Department code or name already in use", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "Forbidden — ADMIN role required", content = @Content(schema = @Schema(hidden = true)))
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseWpp<DepartmentResponseDTO>> updateDepartment(
            @Parameter(description = "Department ID", required = true, example = "1") @PathVariable Long id,
            @Valid @RequestBody DepartmentRequestDTO request) {
        log.info("PUT /api/departments/{}", id);
        return ResponseEntity.ok(ApiResponseWpp.success(departmentService.updateDepartment(id, request),
                "Department updated successfully"));
    }

    // ==================== DELETE Endpoints ====================

    @Operation(summary = "Delete (deactivate) a department", description = "Soft-deletes a department by setting isActive = false. The department and its data are retained. Only ADMIN users can delete departments.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Department deactivated successfully"),
            @ApiResponse(responseCode = "404", description = "Department not found", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "Forbidden — ADMIN role required", content = @Content(schema = @Schema(hidden = true)))
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseWpp<DepartmentResponseDTO>> deleteDepartment(
            @Parameter(description = "Department ID", required = true, example = "1") @PathVariable Long id) {
        log.info("DELETE /api/departments/{}", id);
        return ResponseEntity.ok(
                ApiResponseWpp.success(departmentService.deleteDepartment(id), "Department deactivated successfully"));
    }
}