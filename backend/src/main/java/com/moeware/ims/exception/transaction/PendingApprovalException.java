package com.moeware.ims.exception.transaction;

import org.springframework.http.HttpStatus;

import com.moeware.ims.exception.BaseAppException;

/**
 * Thrown when attempting to use a resource that requires approval
 * but is still in PENDING status.
 *
 * Examples: applying an unapproved stock adjustment, using a pending purchase order.
 *
 * @author MoeWare Team
 */
public class PendingApprovalException extends BaseAppException {

    private final String resourceType;
    private final Long resourceId;

    public PendingApprovalException(String resourceType, Long resourceId) {
        super(String.format("%s with id %d is pending approval and cannot be used",
                resourceType, resourceId));
        this.resourceType = resourceType;
        this.resourceId = resourceId;
    }

    /** Use when the message is free-form and no structured fields are available. */
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

    @Override
    public HttpStatus getHttpStatus() { return HttpStatus.CONFLICT; }

    @Override
    public String getErrorTitle() { return "Pending Approval"; }

    public String getResourceType() { return resourceType; }
    public Long getResourceId()     { return resourceId; }
}