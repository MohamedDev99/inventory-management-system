package com.moeware.ims.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Role entity for Role-Based Access Control (RBAC)
 *
 * @author MoeWare Team
 * @version 1.0
 */
@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "users")
@Schema(description = "Role entity for Role-Based Access Control (RBAC)")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true, length = 50)
    @Schema(description = "Role name (ADMIN, MANAGER, WAREHOUSE_STAFF, VIEWER)", example = "MANAGER", allowableValues = {
            "ADMIN", "MANAGER", "WAREHOUSE_STAFF", "VIEWER" })
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    @Schema(description = "Role description", example = "Warehouse and order management")
    private String description;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    @Schema(description = "Timestamp when the role was created", example = "2026-01-31T10:30:00", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;

    // Bidirectional relationship with User
    @OneToMany(mappedBy = "role", fetch = FetchType.LAZY)
    @Builder.Default
    @Schema(description = "Users associated with this role")
    private Set<User> users = new HashSet<>();

    /**
     * Convenience method to check if this is an admin role
     */
    public boolean isAdmin() {
        return "ADMIN".equals(this.name);
    }

    /**
     * Convenience method to check if this is a manager role
     */
    public boolean isManager() {
        return "MANAGER".equals(this.name);
    }
}