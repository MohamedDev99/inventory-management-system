package com.moeware.ims.exception.auth;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import com.moeware.ims.exception.BaseAppException;

/**
 * Thrown when a user attempts to log in but their account is temporarily
 * locked due to excessive failed login attempts.
 *
 * Carries {@code lockedUntil} so the handler can expose it in the response,
 * letting the client display a meaningful "try again after" message.
 *
 * @author MoeWare Team
 */
public class AccountLockedException extends BaseAppException {

    private final LocalDateTime lockedUntil;

    public AccountLockedException(LocalDateTime lockedUntil) {
        super(String.format(
                "Account is temporarily locked due to too many failed login attempts. Try again after %s",
                lockedUntil));
        this.lockedUntil = lockedUntil;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.LOCKED;
    }

    @Override
    public String getErrorTitle() {
        return "Account Locked";
    }

    public LocalDateTime getLockedUntil() {
        return lockedUntil;
    }
}