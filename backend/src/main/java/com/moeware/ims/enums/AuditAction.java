package com.moeware.ims.enums;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Audit Action enumeration
 * Defines types of auditable actions in the system
 */
@Schema(description = "Type of action recorded in audit log")
public enum AuditAction {
    @Schema(description = "Entity was created")
    CREATE,

    @Schema(description = "Entity was updated")
    UPDATE,

    @Schema(description = "Entity was deleted")
    DELETE,

    @Schema(description = "User logged into the system")
    LOGIN,

    @Schema(description = "User logged out of the system")
    LOGOUT,

    @Schema(description = "Record was approved (e.g., purchase order, adjustment)")
    APPROVE,

    @Schema(description = "Record was rejected")
    REJECT
}