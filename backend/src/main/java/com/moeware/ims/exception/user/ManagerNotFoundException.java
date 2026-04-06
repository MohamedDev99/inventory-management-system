package com.moeware.ims.exception.user;

import org.springframework.http.HttpStatus;

import com.moeware.ims.exception.BaseAppException;

/**
 * Thrown when a user referenced as a manager cannot be found by ID.
 *
 * @author MoeWare Team
 */
public class ManagerNotFoundException extends BaseAppException {

    private final Long managerId;

    public ManagerNotFoundException(Long managerId) {
        super("Manager not found with id: " + managerId);
        this.managerId = managerId;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.NOT_FOUND;
    }

    @Override
    public String getErrorTitle() {
        return "Manager Not Found";
    }

    public Long getManagerId() {
        return managerId;
    }
}