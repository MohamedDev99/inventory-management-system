package com.moeware.ims.entity.inventory;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Supplier entity
 * Represents a supplier of products
 */
@Entity
@Table(name = "suppliers", uniqueConstraints = {
                @UniqueConstraint(columnNames = "name"),
                @UniqueConstraint(columnNames = "code")
}, indexes = {
                @Index(name = "idx_supplier_email", columnList = "email"),
                @Index(name = "idx_supplier_active", columnList = "is_active")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Represents a supplier of products")
public class Supplier extends VersionedEntity {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Schema(description = "Unique identifier for the supplier", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
        private Long id;

        @NotBlank(message = "Supplier name is required")
        @Size(max = 255)
        @Column(nullable = false, unique = true)
        @Schema(description = "Company name of the supplier", example = "Tech Supplies Inc", requiredMode = Schema.RequiredMode.REQUIRED)
        private String name;

        @NotBlank(message = "Supplier code is required")
        @Size(max = 50)
        @Column(nullable = false, unique = true)
        @Schema(description = "Unique code identifying the supplier", example = "SUP001", requiredMode = Schema.RequiredMode.REQUIRED)
        private String code;

        @Size(max = 100)
        @Column(name = "contact_person")
        @Schema(description = "Primary contact person at supplier", example = "John Smith")
        private String contactPerson;

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        @Size(max = 255)
        @Column(nullable = false)
        @Schema(description = "Primary email address for supplier", example = "orders@techsupplies.com", requiredMode = Schema.RequiredMode.REQUIRED)
        private String email;

        @Size(max = 50)
        @Schema(description = "Primary phone number", example = "+1-555-0100")
        private String phone;

        @Column(columnDefinition = "TEXT")
        @Schema(description = "Full street address of supplier", example = "123 Business Park Drive")
        private String address;

        @Size(max = 100)
        @Schema(description = "City", example = "New York")
        private String city;

        @Size(max = 100)
        @Schema(description = "Country", example = "USA")
        private String country;

        @Size(max = 100)
        @Column(name = "payment_terms")
        @Schema(description = "Payment terms with supplier", example = "Net 30")
        private String paymentTerms;

        @Min(1)
        @Max(5)
        @Column(columnDefinition = "INT CHECK (rating >= 1 AND rating <= 5)")
        @Schema(description = "Supplier rating from 1 (poor) to 5 (excellent)", example = "4", minimum = "1", maximum = "5")
        private Integer rating;

        @Column(name = "is_active", nullable = false)
        @Builder.Default
        @Schema(description = "Whether the supplier is currently active", example = "true", defaultValue = "true")
        private Boolean isActive = true;
}