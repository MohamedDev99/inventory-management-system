package com.moeware.ims.exception.inventory;

/**
 * Exception thrown when optimistic locking detects a concurrent modification
 * Occurs when the entity version has changed between read and write operations
 */
public class InventoryConcurrentModificationException extends RuntimeException {

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
                "Concurrent modification detected for %s with id %d. " +
                        "Expected version %d but found %d. Another user may have modified this record.",
                entityType, entityId, expectedVersion, actualVersion));
        this.entityType = entityType;
        this.entityId = entityId;
        this.expectedVersion = expectedVersion;
        this.actualVersion = actualVersion;
    }

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