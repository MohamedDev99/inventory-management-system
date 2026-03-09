package com.moeware.ims.exception.auth;

/**
 * Exception thrown when a user attempts to log in but their account
 * has been deactivated (soft-deleted) by an administrator.
 *
 * @author MoeWare Team
 */
public class AccountDisabledException extends RuntimeException {

    public AccountDisabledException() {
        super("Your account has been deactivated. Please contact an administrator.");
    }

    public AccountDisabledException(String message) {
        super(message);
    }
}