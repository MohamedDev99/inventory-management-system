package com.moeware.ims.exception.handler.staff;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.moeware.ims.exception.staff.department.DepartmentAlreadyExistsException;
import com.moeware.ims.exception.staff.department.DepartmentNotFoundException;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Handles all department-domain exceptions.
 *
 * Dedicated handlers for exceptions with extra response fields:
 * {@link DepartmentNotFoundException} and
 * {@link DepartmentAlreadyExistsException}.
 *
 * {@link InvalidDepartmentOperationException} extends {@link BaseAppException}
 * and returns a standard ErrorResponse — covered automatically by the generic
 * handler in {@link UserExceptionHandler}.
 *
 * @author MoeWare Team
 */
@RestControllerAdvice
@Slf4j
public class DepartmentExceptionHandler {

    @ExceptionHandler(DepartmentNotFoundException.class)
    public ResponseEntity<DepartmentNotFoundErrorResponse> handleDepartmentNotFound(
            DepartmentNotFoundException ex, WebRequest request) {

        log.error("Department not found: {}", ex.getMessage());

        return ResponseEntity.status(ex.getHttpStatus()).body(
                DepartmentNotFoundErrorResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(ex.getHttpStatus().value())
                        .error(ex.getErrorTitle())
                        .message(ex.getMessage())
                        .path(extractPath(request))
                        .departmentId(ex.getDepartmentId())
                        .departmentCode(ex.getDepartmentCode())
                        .build());
    }

    @ExceptionHandler(DepartmentAlreadyExistsException.class)
    public ResponseEntity<DepartmentConflictErrorResponse> handleDepartmentAlreadyExists(
            DepartmentAlreadyExistsException ex, WebRequest request) {

        log.error("Department conflict: {}", ex.getMessage());

        return ResponseEntity.status(ex.getHttpStatus()).body(
                DepartmentConflictErrorResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(ex.getHttpStatus().value())
                        .error(ex.getErrorTitle())
                        .message(ex.getMessage())
                        .path(extractPath(request))
                        .conflictField(ex.getConflictField().name().toLowerCase())
                        .conflictValue(ex.getConflictValue())
                        .build());
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private String extractPath(WebRequest request) {
        return request.getDescription(false).replace("uri=", "");
    }

    // ── Response classes ──────────────────────────────────────────────────────

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Department not found error response")
    public static class DepartmentNotFoundErrorResponse {
        private LocalDateTime timestamp;
        private int status;
        private String error;
        private String message;
        private String path;
        @Schema(description = "Department ID that was not found", example = "3")
        private Long departmentId;
        @Schema(description = "Department code that was not found", example = "DEPT-HR")
        private String departmentCode;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Department conflict error response")
    public static class DepartmentConflictErrorResponse {
        private LocalDateTime timestamp;
        private int status;
        private String error;
        private String message;
        private String path;
        @Schema(description = "Field that caused the conflict", example = "department_code", allowableValues = {
                "department_code", "name" })
        private String conflictField;
        @Schema(description = "Value that caused the conflict", example = "DEPT-HR")
        private String conflictValue;
    }
}