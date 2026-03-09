package com.moeware.ims.exception.auth;

import java.time.LocalDateTime;

/**
 * Exception thrown when a user attempts to log in but their account
 * is temporarily locked due to excessive failed login attempts.
 *
 * @author MoeWare Team
 */
public class AccountLockedException extends RuntimeException {

    private final LocalDateTime lockedUntil;

    public AccountLockedException(LocalDateTime lockedUntil) {
        super(String.format(
                "Account is temporarily locked due to too many failed login attempts. Try again after %s",
                lockedUntil));
        this.lockedUntil = lockedUntil;
    }

    public LocalDateTime getLockedUntil() {
        return lockedUntil;
    }
}