package com.moeware.ims.exception.handler;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.moeware.ims.exception.user.UserAlreadyExistsException;
import com.moeware.ims.exception.user.UserNotFoundException;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.extern.slf4j.Slf4j;

/**
 * Handles all user-domain exceptions.
 *
 * Most exceptions are routed through {@link #handleBaseAppException} —
 * a single generic handler that reads HTTP status and error title directly
 * from the exception, so no new handler method is needed when new user
 * exceptions are added.
 *
 * Only {@link UserAlreadyExistsException} and {@link UserNotFoundException}
 * get their own methods because they enrich the response with extra fields.
 *
 * @author MoeWare Team
 */
@RestControllerAdvice
@Slf4j
public class UserExceptionHandler {

    // ── Specific handlers (richer response bodies) ────────────────────────────

    /**
     * Enriches the conflict response with the field that caused the conflict
     * (USERNAME or EMAIL), making it easier for the frontend to highlight
     * the correct input field.
     */
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<UserConflictErrorResponse> handleUserAlreadyExists(
            UserAlreadyExistsException ex,
            WebRequest request) {

        log.error("User conflict on {}: {}", ex.getConflictField(), ex.getMessage());

        UserConflictErrorResponse body = UserConflictErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(ex.getHttpStatus().value())
                .error(ex.getErrorTitle())
                .message(ex.getMessage())
                .path(extractPath(request))
                .conflictField(ex.getConflictField() != null ? ex.getConflictField().name().toLowerCase() : null)
                .conflictValue(ex.getConflictValue())
                .build();

        return ResponseEntity.status(ex.getHttpStatus()).body(body);
    }

    /**
     * Enriches the not-found response with userId or username to assist
     * debugging and structured API error handling.
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<UserNotFoundErrorResponse> handleUserNotFound(
            UserNotFoundException ex,
            WebRequest request) {

        log.error("User not found: {}", ex.getMessage());

        UserNotFoundErrorResponse body = UserNotFoundErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(ex.getHttpStatus().value())
                .error(ex.getErrorTitle())
                .message(ex.getMessage())
                .path(extractPath(request))
                .userId(ex.getUserId())
                .username(ex.getUsername())
                .build();

        return ResponseEntity.status(ex.getHttpStatus()).body(body);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private String extractPath(WebRequest request) {
        return request.getDescription(false).replace("uri=", "");
    }

    // ── Response classes (user-domain specific) ───────────────────────────────

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    @Schema(description = "User conflict error response")
    public static class UserConflictErrorResponse {
        private LocalDateTime timestamp;
        private int status;
        private String error;
        private String message;
        private String path;

        @Schema(description = "The field that caused the conflict", example = "email", allowableValues = {
                "username", "email" })
        private String conflictField;

        @Schema(description = "The conflicting value", example = "john@example.com")
        private String conflictValue;
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    @Schema(description = "User not found error response")
    public static class UserNotFoundErrorResponse {
        private LocalDateTime timestamp;
        private int status;
        private String error;
        private String message;
        private String path;

        @Schema(description = "User ID that was not found", example = "42")
        private Long userId;

        @Schema(description = "Username that was not found", example = "john_doe")
        private String username;
    }
}