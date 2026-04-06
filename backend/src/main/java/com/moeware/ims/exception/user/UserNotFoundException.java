package com.moeware.ims.exception.user;

import org.springframework.http.HttpStatus;

import com.moeware.ims.exception.BaseAppException;

/**
 * Thrown when a user cannot be found by ID or username.
 *
 * Replaces the generic {@code ResourceNotFoundException("User not found...")}
 * calls inside {@link com.moeware.ims.service.UserService}, giving the
 * handler a specific, domain-typed exception to work with.
 *
 * @author MoeWare Team
 */
public class UserNotFoundException extends BaseAppException {

    private final Long userId;
    private final String username;

    // ── Lookup by ID ─────────────────────────────────────────────────────────

    public UserNotFoundException(Long userId) {
        super("User not found with id: " + userId);
        this.userId = userId;
        this.username = null;
    }

    // ── Lookup by username ────────────────────────────────────────────────────

    public UserNotFoundException(String username) {
        super("User not found: " + username);
        this.userId = null;
        this.username = username;
    }

    // ── BaseAppException contract ─────────────────────────────────────────────

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.NOT_FOUND;
    }

    @Override
    public String getErrorTitle() {
        return "User Not Found";
    }

    // ── Accessors ─────────────────────────────────────────────────────────────

    /** Non-null when the lookup was by ID. */
    public Long getUserId() {
        return userId;
    }

    /** Non-null when the lookup was by username. */
    public String getUsername() {
        return username;
    }
}