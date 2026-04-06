package com.moeware.ims.exception.inventory;

import org.springframework.http.HttpStatus;

import com.moeware.ims.exception.BaseAppException;

/**
 * Thrown when optimistic locking detects a concurrent modification — the entity
 * version changed between the read and write operations, meaning another
 * transaction already committed a change to the same record.
 *
 * @author MoeWare Team
 */
public class InventoryConcurrentModificationException extends BaseAppException {

    private final String entityType;
    private final Long entityId;
    private final Integer expectedVersion;
    private final Integer actualVersion;

    public InventoryConcurrentModificationException(
            String entityType,
            Long entityId,
            Integer expectedVersion,
            Integer actualVersion) {
        super(String.format(
                "Concurrent modification detected for %s with id %d. "
                        + "Expected version %d but found %d. "
                        + "Another user may have modified this record.",
                entityType, entityId, expectedVersion, actualVersion));
        this.entityType = entityType;
        this.entityId = entityId;
        this.expectedVersion = expectedVersion;
        this.actualVersion = actualVersion;
    }

    /**
     * Use when version details are unavailable (e.g., caught from JPA internals).
     */
    public InventoryConcurrentModificationException(String message) {
        super(message);
        this.entityType = null;
        this.entityId = null;
        this.expectedVersion = null;
        this.actualVersion = null;
    }

    public InventoryConcurrentModificationException(String message, Throwable cause) {
        super(message, cause);
        this.entityType = null;
        this.entityId = null;
        this.expectedVersion = null;
        this.actualVersion = null;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.CONFLICT;
    }

    @Override
    public String getErrorTitle() {
        return "Concurrent Modification";
    }

    public String getEntityType() {
        return entityType;
    }

    public Long getEntityId() {
        return entityId;
    }

    public Integer getExpectedVersion() {
        return expectedVersion;
    }

    public Integer getActualVersion() {
        return actualVersion;
    }
}