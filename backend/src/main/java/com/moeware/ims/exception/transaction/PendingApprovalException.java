package com.moeware.ims.exception.transaction;

/**
 * Exception thrown when attempting to apply or use a resource that requires
 * approval
 * but is still in PENDING status
 * Examples: applying unapproved stock adjustment, using pending purchase order
 */
public class PendingApprovalException extends RuntimeException {

    private final Long resourceId;
    private final String resourceType;

    public PendingApprovalException(String resourceType, Long resourceId) {
        super(String.format("%s with id %d is pending approval and cannot be used",
                resourceType, resourceId));
        this.resourceType = resourceType;
        this.resourceId = resourceId;
    }

    public PendingApprovalException(String message) {
        super(message);
        this.resourceType = null;
        this.resourceId = null;
    }

    public PendingApprovalException(String message, Throwable cause) {
        super(message, cause);
        this.resourceType = null;
        this.resourceId = null;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public String getResourceType() {
        return resourceType;
    }
}