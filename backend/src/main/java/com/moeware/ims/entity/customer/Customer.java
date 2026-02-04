package com.moeware.ims.entity.customer;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "customers", indexes = {
        @Index(name = "idx_customers_code", columnList = "customer_code", unique = true),
        @Index(name = "idx_customers_email", columnList = "email", unique = true),
        @Index(name = "idx_customers_company", columnList = "company_name"),
        @Index(name = "idx_customers_type", columnList = "customer_type"),
        @Index(name = "idx_customers_active", columnList = "id, contact_name")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Customer code is required")
    @Size(max = 50, message = "Customer code must not exceed 50 characters")
    @Column(name = "customer_code", nullable = false, unique = true, length = 50)
    private String customerCode;

    @Size(max = 255, message = "Company name must not exceed 255 characters")
    @Column(name = "company_name", length = 255)
    private String companyName;

    @NotBlank(message = "Contact name is required")
    @Size(max = 255, message = "Contact name must not exceed 255 characters")
    @Column(name = "contact_name", nullable = false, length = 255)
    private String contactName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Size(max = 50, message = "Phone must not exceed 50 characters")
    @Column(length = 50)
    private String phone;

    @Size(max = 50, message = "Mobile must not exceed 50 characters")
    @Column(length = 50)
    private String mobile;

    // Billing Address
    @Column(name = "billing_address", columnDefinition = "TEXT")
    private String billingAddress;

    @Size(max = 100, message = "Billing city must not exceed 100 characters")
    @Column(name = "billing_city", length = 100)
    private String billingCity;

    @Size(max = 100, message = "Billing state must not exceed 100 characters")
    @Column(name = "billing_state", length = 100)
    private String billingState;

    @Size(max = 100, message = "Billing country must not exceed 100 characters")
    @Column(name = "billing_country", length = 100)
    private String billingCountry;

    @Size(max = 20, message = "Billing postal code must not exceed 20 characters")
    @Column(name = "billing_postal_code", length = 20)
    private String billingPostalCode;

    // Shipping Address
    @Column(name = "shipping_address", columnDefinition = "TEXT")
    private String shippingAddress;

    @Size(max = 100, message = "Shipping city must not exceed 100 characters")
    @Column(name = "shipping_city", length = 100)
    private String shippingCity;

    @Size(max = 100, message = "Shipping state must not exceed 100 characters")
    @Column(name = "shipping_state", length = 100)
    private String shippingState;

    @Size(max = 100, message = "Shipping country must not exceed 100 characters")
    @Column(name = "shipping_country", length = 100)
    private String shippingCountry;

    @Size(max = 20, message = "Shipping postal code must not exceed 20 characters")
    @Column(name = "shipping_postal_code", length = 20)
    private String shippingPostalCode;

    @Column(name = "credit_limit", precision = 12, scale = 2)
    private BigDecimal creditLimit;

    @Size(max = 100, message = "Payment terms must not exceed 100 characters")
    @Column(name = "payment_terms", length = 100)
    private String paymentTerms; // Net 30, COD, etc.

    @NotBlank(message = "Customer type is required")
    @Size(max = 20, message = "Customer type must not exceed 20 characters")
    @Column(name = "customer_type", nullable = false, length = 20)
    private String customerType; // RETAIL, WHOLESALE, CORPORATE

    @Size(max = 50, message = "Tax ID must not exceed 50 characters")
    @Column(name = "tax_id", length = 50)
    private String taxId;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    // @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    // @Builder.Default
    // private Set<SalesOrder> salesOrders = new HashSet<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Helper methods
    public String getFullBillingAddress() {
        if (billingAddress == null)
            return "";
        StringBuilder sb = new StringBuilder(billingAddress);
        if (billingCity != null)
            sb.append(", ").append(billingCity);
        if (billingState != null)
            sb.append(", ").append(billingState);
        if (billingPostalCode != null)
            sb.append(" ").append(billingPostalCode);
        if (billingCountry != null)
            sb.append(", ").append(billingCountry);
        return sb.toString();
    }

    public String getFullShippingAddress() {
        if (shippingAddress == null)
            return "";
        StringBuilder sb = new StringBuilder(shippingAddress);
        if (shippingCity != null)
            sb.append(", ").append(shippingCity);
        if (shippingState != null)
            sb.append(", ").append(shippingState);
        if (shippingPostalCode != null)
            sb.append(" ").append(shippingPostalCode);
        if (shippingCountry != null)
            sb.append(", ").append(shippingCountry);
        return sb.toString();
    }

    public boolean isRetail() {
        return "RETAIL".equalsIgnoreCase(customerType);
    }

    public boolean isWholesale() {
        return "WHOLESALE".equalsIgnoreCase(customerType);
    }

    public boolean isCorporate() {
        return "CORPORATE".equalsIgnoreCase(customerType);
    }
}