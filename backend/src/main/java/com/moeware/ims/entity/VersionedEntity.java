package com.moeware.ims.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.Setter;

/**
 * Versioned entity class providing audit fields and optimistic locking for all
 * entities
 * Automatically tracks creation and modification metadata, and prevents lost
 * updates
 */
@MappedSuperclass
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@Schema(description = "Versioned entity with audit fields and version control inherited by all entities")
public abstract class VersionedEntity {

    @Version
    @Column(name = "version", nullable = false)
    @Schema(description = "Version number for optimistic locking (prevents concurrent update conflicts)", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long version;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    @Schema(description = "Timestamp when the entity was created", example = "2026-01-31T10:30:00", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    @Schema(description = "Timestamp when the entity was last updated", example = "2026-01-31T14:45:00", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime updatedAt;

    @CreatedBy
    @Column(name = "created_by", updatable = false)
    @Schema(description = "Username of the user who created this entity", example = "john.doe@company.com", accessMode = Schema.AccessMode.READ_ONLY)
    private String createdBy;

    @LastModifiedBy
    @Column(name = "updated_by")
    @Schema(description = "Username of the user who last updated this entity", example = "jane.manager@company.com", accessMode = Schema.AccessMode.READ_ONLY)
    private String updatedBy;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
    }

    // @PreUpdate
    // protected void onUpdate() {
    // updatedAt = LocalDateTime.now();
    // }
}