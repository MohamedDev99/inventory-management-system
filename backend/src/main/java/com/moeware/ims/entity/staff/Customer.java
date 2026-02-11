package com.moeware.ims.entity.customer;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.moeware.ims.entity.VersionedEntity;
import com.moeware.ims.entity.transaction.SalesOrder;

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
@Schema(description = "Customer information including contact details and addresses for billing and shipping")
public class Customer extends VersionedEntity {

    @Schema(description = "Unique identifier", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "Unique customer code", example = "CUST-001", required = true, maxLength = 50)
    @NotBlank(message = "Customer code is required")
    @Size(max = 50, message = "Customer code must not exceed 50 characters")
    @Column(name = "customer_code", nullable = false, unique = true, length = 50)
    private String customerCode;

    @Schema(description = "Company name for corporate customers", example = "ABC Corporation", maxLength = 255)
    @Size(max = 255, message = "Company name must not exceed 255 characters")
    @Column(name = "company_name", length = 255)
    private String companyName;

    @Schema(description = "Primary contact person name", example = "John Doe", required = true, maxLength = 255)
    @NotBlank(message = "Contact name is required")
    @Size(max = 255, message = "Contact name must not exceed 255 characters")
    @Column(name = "contact_name", nullable = false, length = 255)
    private String contactName;

    @Schema(description = "Contact email address", example = "john.doe@email.com", required = true, maxLength = 255, format = "email")
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Schema(description = "Primary phone number", example = "+1-555-0100", maxLength = 50)
    @Size(max = 50, message = "Phone must not exceed 50 characters")
    @Column(length = 50)
    private String phone;

    @Schema(description = "Mobile phone number", example = "+1-555-0101", maxLength = 50)
    @Size(max = 50, message = "Mobile must not exceed 50 characters")
    @Column(length = 50)
    private String mobile;

    // Billing Address
    @Schema(description = "Billing street address", example = "123 Business St")
    @Column(name = "billing_address", columnDefinition = "TEXT")
    private String billingAddress;

    @Schema(description = "Billing city", example = "New York", maxLength = 100)
    @Size(max = 100, message = "Billing city must not exceed 100 characters")
    @Column(name = "billing_city", length = 100)
    private String billingCity;

    @Schema(description = "Billing state or province", example = "NY", maxLength = 100)
    @Size(max = 100, message = "Billing state must not exceed 100 characters")
    @Column(name = "billing_state", length = 100)
    private String billingState;

    @Schema(description = "Billing country", example = "USA", maxLength = 100)
    @Size(max = 100, message = "Billing country must not exceed 100 characters")
    @Column(name = "billing_country", length = 100)
    private String billingCountry;

    @Schema(description = "Billing postal code", example = "10001", maxLength = 20)
    @Size(max = 20, message = "Billing postal code must not exceed 20 characters")
    @Column(name = "billing_postal_code", length = 20)
    private String billingPostalCode;

    // Shipping Address
    @Schema(description = "Shipping street address", example = "456 Delivery Ave")
    @Column(name = "shipping_address", columnDefinition = "TEXT")
    private String shippingAddress;

    @Schema(description = "Shipping city", example = "Boston", maxLength = 100)
    @Size(max = 100, message = "Shipping city must not exceed 100 characters")
    @Column(name = "shipping_city", length = 100)
    private String shippingCity;

    @Schema(description = "Shipping state or province", example = "MA", maxLength = 100)
    @Size(max = 100, message = "Shipping state must not exceed 100 characters")
    @Column(name = "shipping_state", length = 100)
    private String shippingState;

    @Schema(description = "Shipping country", example = "USA", maxLength = 100)
    @Size(max = 100, message = "Shipping country must not exceed 100 characters")
    @Column(name = "shipping_country", length = 100)
    private String shippingCountry;

    @Schema(description = "Shipping postal code", example = "02101", maxLength = 20)
    @Size(max = 20, message = "Shipping postal code must not exceed 20 characters")
    @Column(name = "shipping_postal_code", length = 20)
    private String shippingPostalCode;

    @Schema(description = "Customer credit limit", example = "10000.00", minimum = "0")
    @Column(name = "credit_limit", precision = 12, scale = 2)
    private BigDecimal creditLimit;

    @Schema(description = "Payment terms", example = "Net 30", maxLength = 100, allowableValues = { "COD", "Net 30",
            "Net 45", "Net 60", "Credit Card" })
    @Size(max = 100, message = "Payment terms must not exceed 100 characters")
    @Column(name = "payment_terms", length = 100)
    private String paymentTerms; // Net 30, COD, etc.

    @Schema(description = "Customer type classification", example = "CORPORATE", required = true, maxLength = 20, allowableValues = {
            "RETAIL", "WHOLESALE", "CORPORATE" })
    @NotBlank(message = "Customer type is required")
    @Size(max = 20, message = "Customer type must not exceed 20 characters")
    @Column(name = "customer_type", nullable = false, length = 20)
    private String customerType; // RETAIL, WHOLESALE, CORPORATE

    @Schema(description = "Tax identification number", example = "12-3456789", maxLength = 50)
    @Size(max = 50, message = "Tax ID must not exceed 50 characters")
    @Column(name = "tax_id", length = 50)
    private String taxId;

    @Schema(description = "Whether the customer account is active", example = "true", required = true)
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Schema(description = "Sales orders placed by this customer", accessMode = Schema.AccessMode.READ_ONLY)
    @OneToMany(mappedBy = "customer", cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @Builder.Default
    private Set<SalesOrder> salesOrders = new HashSet<>();

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