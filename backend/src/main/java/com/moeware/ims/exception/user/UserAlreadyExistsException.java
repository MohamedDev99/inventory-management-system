package com.moeware.ims.exception.user;

import org.springframework.http.HttpStatus;

import com.moeware.ims.exception.BaseAppException;

/**
 * Thrown when attempting to register a user with a username or email
 * that is already taken.
 *
 * Carries a {@link ConflictField} so the handler (and API consumer)
 * can tell exactly which field caused the conflict.
 *
 * @author MoeWare Team
 */
public class UserAlreadyExistsException extends BaseAppException {

    public enum ConflictField {
        USERNAME, EMAIL
    }

    private final ConflictField conflictField;
    private final String conflictValue;

    public UserAlreadyExistsException(ConflictField conflictField, String conflictValue) {
        super(buildMessage(conflictField, conflictValue));
        this.conflictField = conflictField;
        this.conflictValue = conflictValue;
    }

    public UserAlreadyExistsException(ConflictField conflictField, String conflictValue, Throwable cause) {
        super(buildMessage(conflictField, conflictValue), cause);
        this.conflictField = conflictField;
        this.conflictValue = conflictValue;
    }

    // ── Legacy constructor so existing callers keep compiling ─────────────────
    // Remove once all call sites are updated to the typed constructor above.
    @Deprecated(forRemoval = true)
    public UserAlreadyExistsException(String message) {
        super(message);
        this.conflictField = null;
        this.conflictValue = null;
    }

    // ── BaseAppException contract ─────────────────────────────────────────────

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.CONFLICT;
    }

    @Override
    public String getErrorTitle() {
        return "User Already Exists";
    }

    // ── Accessors ─────────────────────────────────────────────────────────────

    public ConflictField getConflictField() {
        return conflictField;
    }

    public String getConflictValue() {
        return conflictValue;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private static String buildMessage(ConflictField field, String value) {
        return switch (field) {
            case USERNAME -> "Username already exists: " + value;
            case EMAIL -> "Email already exists: " + value;
        };
    }
}