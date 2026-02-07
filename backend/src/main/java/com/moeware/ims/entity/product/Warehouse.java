package com.moeware.ims.entity.product;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import io.swagger.v3.oas.annotations.media.Schema;

import com.moeware.ims.entity.BaseEntity;
import com.moeware.ims.entity.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "warehouses", indexes = {
        @Index(name = "idx_warehouses_code", columnList = "code", unique = true),
        @Index(name = "idx_warehouses_manager", columnList = "manager_id"),
        @Index(name = "idx_warehouses_active", columnList = "id, name")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Physical warehouse location for inventory storage and management")
public class Warehouse extends BaseEntity {

    @Schema(description = "Unique identifier", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "Warehouse name", example = "Main Warehouse", required = true, maxLength = 100)
    @NotBlank(message = "Warehouse name is required")
    @Size(max = 100, message = "Warehouse name must not exceed 100 characters")
    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Schema(description = "Short warehouse code", example = "WH001", required = true, maxLength = 20)
    @NotBlank(message = "Warehouse code is required")
    @Size(max = 20, message = "Warehouse code must not exceed 20 characters")
    @Column(nullable = false, unique = true, length = 20)
    private String code;

    @Schema(description = "Street address", example = "123 Storage Street", required = true)
    @NotBlank(message = "Address is required")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String address;

    @Schema(description = "City", example = "New York", required = true, maxLength = 100)
    @NotBlank(message = "City is required")
    @Size(max = 100, message = "City must not exceed 100 characters")
    @Column(nullable = false, length = 100)
    private String city;

    @Schema(description = "State or province", example = "NY", maxLength = 100)
    @Size(max = 100, message = "State must not exceed 100 characters")
    @Column(length = 100)
    private String state;

    @Schema(description = "Country", example = "USA", required = true, maxLength = 100)
    @NotBlank(message = "Country is required")
    @Size(max = 100, message = "Country must not exceed 100 characters")
    @Column(nullable = false, length = 100)
    private String country;

    @Schema(description = "Postal or ZIP code", example = "10001", maxLength = 20)
    @Size(max = 20, message = "Postal code must not exceed 20 characters")
    @Column(length = 20)
    private String postalCode;

    @Schema(description = "Warehouse manager user", implementation = User.class)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private User manager;

    @Schema(description = "Storage capacity in square feet", example = "50000.00", minimum = "0")
    @Column(precision = 12, scale = 2)
    private BigDecimal capacity; // Storage capacity in square feet

    @Schema(description = "Whether the warehouse is operational", example = "true", required = true)
    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Schema(description = "Inventory items stored in this warehouse", accessMode = Schema.AccessMode.READ_ONLY)
    @OneToMany(mappedBy = "warehouse", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<InventoryItem> inventoryItems = new HashSet<>();

    // Helper methods
    public String getFullAddress() {
        StringBuilder sb = new StringBuilder(address);
        sb.append(", ").append(city);
        if (state != null && !state.isEmpty()) {
            sb.append(", ").append(state);
        }
        if (postalCode != null && !postalCode.isEmpty()) {
            sb.append(" ").append(postalCode);
        }
        sb.append(", ").append(country);
        return sb.toString();
    }

    public int getTotalProductTypes() {
        return inventoryItems.size();
    }

    public int getTotalStockUnits() {
        return inventoryItems.stream()
                .mapToInt(InventoryItem::getQuantity)
                .sum();
    }
}