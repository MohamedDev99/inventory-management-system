package com.moeware.ims.exception.handler;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.moeware.ims.exception.auth.AccountDisabledException;
import com.moeware.ims.exception.auth.AccountLockedException;
import com.moeware.ims.exception.auth.InvalidCredentialsException;
import com.moeware.ims.exception.auth.InvalidTokenException;
import com.moeware.ims.exception.handler.GlobalExceptionHandler.ErrorResponse;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Handles all auth-domain exceptions.
 *
 * {@link AccountDisabledException}, {@link InvalidCredentialsException}, and
 * {@link InvalidTokenException} all extend {@link BaseAppException} and return
 * a standard {@link ErrorResponse} — covered automatically by the generic
 * handler in {@link UserExceptionHandler}.
 *
 * Only {@link AccountLockedException} needs its own method because it exposes
 * the {@code lockedUntil} timestamp in the response body.
 *
 * Spring Security's {@link BadCredentialsException} and
 * {@link UsernameNotFoundException} are also mapped here so they produce the
 * same shaped response as {@link InvalidCredentialsException} instead of
 * falling through to the global 500 handler.
 *
 * @author MoeWare Team
 */
@RestControllerAdvice
@Slf4j
public class AuthExceptionHandler {

    // ── Specific handler (richer response body) ───────────────────────────────

    @ExceptionHandler(AccountLockedException.class)
    public ResponseEntity<AccountLockedErrorResponse> handleAccountLocked(
            AccountLockedException ex,
            WebRequest request) {

        log.warn("Account locked: {}", ex.getMessage());

        AccountLockedErrorResponse body = AccountLockedErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(ex.getHttpStatus().value())
                .error(ex.getErrorTitle())
                .message(ex.getMessage())
                .path(extractPath(request))
                .lockedUntil(ex.getLockedUntil())
                .build();

        return ResponseEntity.status(ex.getHttpStatus()).body(body);
    }

    // ── Spring Security exceptions ────────────────────────────────────────────
    // These are thrown directly by Spring internals (not our code), so they
    // don't extend BaseAppException. Map them to 401 here to keep responses
    // consistent.

    @ExceptionHandler({ BadCredentialsException.class, UsernameNotFoundException.class })
    public ResponseEntity<ErrorResponse> handleSpringAuthExceptions(
            Exception ex,
            WebRequest request) {

        log.error("Spring Security auth error: {}", ex.getMessage());

        return ResponseEntity.status(401).body(ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(401)
                .error("Unauthorized")
                .message("Invalid username or password")
                .path(extractPath(request))
                .build());
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private String extractPath(WebRequest request) {
        return request.getDescription(false).replace("uri=", "");
    }

    // ── Response class ────────────────────────────────────────────────────────

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Account locked error response")
    public static class AccountLockedErrorResponse {
        private LocalDateTime timestamp;
        private int status;
        private String error;
        private String message;
        private String path;

        @Schema(description = "Timestamp when the account lock expires", example = "2026-03-09T10:45:00")
        private java.time.LocalDateTime lockedUntil;
    }
}