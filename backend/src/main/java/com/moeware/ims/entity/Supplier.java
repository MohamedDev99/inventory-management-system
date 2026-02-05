package com.moeware.ims.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

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
public class Supplier extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Supplier name is required")
    @Size(max = 255)
    @Column(nullable = false, unique = true)
    private String name;

    @NotBlank(message = "Supplier code is required")
    @Size(max = 50)
    @Column(nullable = false, unique = true)
    private String code;

    @Size(max = 100)
    @Column(name = "contact_person")
    private String contactPerson;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 255)
    @Column(nullable = false)
    private String email;

    @Size(max = 50)
    private String phone;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Size(max = 100)
    private String city;

    @Size(max = 100)
    private String country;

    @Size(max = 100)
    @Column(name = "payment_terms")
    private String paymentTerms;

    @Min(1)
    @Max(5)
    @Column(columnDefinition = "INT CHECK (rating >= 1 AND rating <= 5)")
    private Integer rating;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;
}