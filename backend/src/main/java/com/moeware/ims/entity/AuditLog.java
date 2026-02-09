package com.moeware.ims.entity;

import com.moeware.ims.enums.AuditAction;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.Type;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * AuditLog entity
 * Represents an audit log entry
 */
@Entity
@Table(name = "audit_logs", indexes = {
                @Index(name = "idx_audit_entity", columnList = "entity_type, entity_id"),
                @Index(name = "idx_audit_performed_by", columnList = "performed_by"),
                @Index(name = "idx_audit_action", columnList = "action"),
                @Index(name = "idx_audit_created_at", columnList = "created_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Schema(description = "Audit log entry")
public class AuditLog extends AppendOnlyEntity {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @EqualsAndHashCode.Include
        @Schema(description = "Unique identifier for the audit log entry", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
        private Long id;

        @NotBlank(message = "Entity type is required")
        @Size(max = 50)
        @Column(name = "entity_type", length = 50, nullable = false)
        @Schema(description = "Type of entity that was modified", example = "PRODUCT", requiredMode = Schema.RequiredMode.REQUIRED)
        private String entityType;

        @NotNull(message = "Entity ID is required")
        @Column(name = "entity_id", nullable = false)
        @Schema(description = "ID of the entity that was modified", example = "123", requiredMode = Schema.RequiredMode.REQUIRED)
        private Long entityId;

        @NotNull(message = "Action is required")
        @Enumerated(EnumType.STRING)
        @Column(length = 20, nullable = false)
        @Schema(description = "Action performed on the entity", example = "UPDATE", allowableValues = { "CREATE",
                        "UPDATE",
                        "DELETE", "LOGIN", "LOGOUT", "APPROVE", "REJECT" }, requiredMode = Schema.RequiredMode.REQUIRED)
        private AuditAction action;

        @Type(JsonBinaryType.class)
        @Column(name = "old_values", columnDefinition = "jsonb")
        @Schema(description = "Previous values before the change (JSON format)", example = "{\"unitPrice\": 99.00, \"quantity\": 100}")
        private Map<String, Object> oldValues;

        @Type(JsonBinaryType.class)
        @Column(name = "new_values", columnDefinition = "jsonb")
        @Schema(description = "New values after the change (JSON format)", example = "{\"unitPrice\": 199.00, \"quantity\": 100}")
        private Map<String, Object> newValues;

        @NotNull(message = "Performer is required")
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "performed_by", nullable = false)
        @Schema(description = "User who performed the action", requiredMode = Schema.RequiredMode.REQUIRED)
        private User performedBy;

        @Size(max = 45)
        @Column(name = "ip_address", length = 45)
        @Schema(description = "IP address from which the action was performed", example = "192.168.1.100")
        private String ipAddress;

        @Column(name = "user_agent", columnDefinition = "TEXT")
        @Schema(description = "Browser user agent string", example = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/120.0")
        private String userAgent;

        @CreatedDate
        @Column(name = "created_at", nullable = false, updatable = false)
        @Builder.Default
        @Schema(description = "Timestamp when the action occurred", example = "2026-01-31T10:30:00", accessMode = Schema.AccessMode.READ_ONLY)
        private LocalDateTime createdAt = LocalDateTime.now();

}