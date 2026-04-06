package com.moeware.ims.exception.handler.staff;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.moeware.ims.exception.staff.employee.EmployeeAlreadyExistsException;
import com.moeware.ims.exception.staff.employee.EmployeeNotFoundException;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Handles all employee-domain exceptions.
 *
 * Dedicated handlers for exceptions with extra response fields:
 * {@link EmployeeNotFoundException} and {@link EmployeeAlreadyExistsException}.
 *
 * {@link InvalidEmployeeOperationException} extends {@link BaseAppException}
 * and returns a standard ErrorResponse — covered automatically by the generic
 * handler in {@link UserExceptionHandler}. No dedicated method needed.
 *
 * @author MoeWare Team
 */
@RestControllerAdvice
@Slf4j
public class EmployeeExceptionHandler {

    @ExceptionHandler(EmployeeNotFoundException.class)
    public ResponseEntity<EmployeeNotFoundErrorResponse> handleEmployeeNotFound(
            EmployeeNotFoundException ex, WebRequest request) {

        log.error("Employee not found: {}", ex.getMessage());

        return ResponseEntity.status(ex.getHttpStatus()).body(
                EmployeeNotFoundErrorResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(ex.getHttpStatus().value())
                        .error(ex.getErrorTitle())
                        .message(ex.getMessage())
                        .path(extractPath(request))
                        .employeeId(ex.getEmployeeId())
                        .employeeCode(ex.getEmployeeCode())
                        .build());
    }

    @ExceptionHandler(EmployeeAlreadyExistsException.class)
    public ResponseEntity<EmployeeConflictErrorResponse> handleEmployeeAlreadyExists(
            EmployeeAlreadyExistsException ex, WebRequest request) {

        log.error("Employee conflict: {}", ex.getMessage());

        return ResponseEntity.status(ex.getHttpStatus()).body(
                EmployeeConflictErrorResponse.builder()
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
    @Schema(description = "Employee not found error response")
    public static class EmployeeNotFoundErrorResponse {
        private LocalDateTime timestamp;
        private int status;
        private String error;
        private String message;
        private String path;
        @Schema(description = "Employee ID that was not found", example = "15")
        private Long employeeId;
        @Schema(description = "Employee code that was not found", example = "EMP-001")
        private String employeeCode;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Employee conflict error response")
    public static class EmployeeConflictErrorResponse {
        private LocalDateTime timestamp;
        private int status;
        private String error;
        private String message;
        private String path;
        @Schema(description = "Field that caused the conflict", example = "email", allowableValues = { "employee_code",
                "email", "user_linked" })
        private String conflictField;
        @Schema(description = "Value that caused the conflict", example = "john@example.com")
        private String conflictValue;
    }
}