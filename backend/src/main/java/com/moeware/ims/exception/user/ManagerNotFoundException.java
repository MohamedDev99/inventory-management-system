package com.moeware.ims.exception.user;

/**
 * Exception thrown when a manager (user) is not found during warehouse
 * operations
 */
public class ManagerNotFoundException extends RuntimeException {

    private final Long managerId;

    public ManagerNotFoundException(Long managerId) {
        super("Manager (user) not found with id: " + managerId);
        this.managerId = managerId;
    }

    public ManagerNotFoundException(String message) {
        super(message);
        this.managerId = null;
    }

    public ManagerNotFoundException(String message, Throwable cause) {
        super(message, cause);
        this.managerId = null;
    }

    public Long getManagerId() {
        return managerId;
    }
}