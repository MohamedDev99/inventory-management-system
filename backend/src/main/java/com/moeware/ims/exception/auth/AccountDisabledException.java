package com.moeware.ims.exception.auth;

import org.springframework.http.HttpStatus;

import com.moeware.ims.exception.BaseAppException;

/**
 * Thrown when a user attempts to log in but their account
 * has been deactivated by an administrator.
 *
 * @author MoeWare Team
 */
public class AccountDisabledException extends BaseAppException {

    public AccountDisabledException() {
        super("Your account has been deactivated. Please contact an administrator.");
    }

    public AccountDisabledException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.FORBIDDEN;
    }

    @Override
    public String getErrorTitle() {
        return "Account Disabled";
    }
}