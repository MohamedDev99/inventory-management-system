package com.moeware.ims.mapper.staff.customer;

import org.springframework.stereotype.Component;

import com.moeware.ims.dto.staff.customer.CustomerCreateRequest;
import com.moeware.ims.dto.staff.customer.CustomerResponse;
import com.moeware.ims.dto.staff.customer.CustomerUpdateRequest;
import com.moeware.ims.entity.staff.Customer;

/**
 * Mapper utility for converting between Customer entity and the typed
 * request/response DTOs.
 *
 * <ul>
 * <li>{@link #toResponse(Customer)} — entity → read DTO</li>
 * <li>{@link #toEntity(CustomerCreateRequest)} — create payload → new
 * entity</li>
 * <li>{@link #applyUpdate(Customer, CustomerUpdateRequest)} — patch/put payload
 * → existing entity</li>
 * </ul>
 */
@Component
public class CustomerMapper {

    /**
     * Convert a Customer entity to a {@link CustomerResponse}.
     *
     * @param customer Customer entity (must not be null)
     * @return CustomerResponse, or null if customer is null
     */
    public CustomerResponse toResponse(Customer customer) {
        if (customer == null) {
            return null;
        }

        return CustomerResponse.builder()
                .id(customer.getId())
                .customerCode(customer.getCustomerCode())
                .companyName(customer.getCompanyName())
                .contactName(customer.getContactName())
                .email(customer.getEmail())
                .phone(customer.getPhone())
                .mobile(customer.getMobile())
                .billingAddress(customer.getBillingAddress())
                .billingCity(customer.getBillingCity())
                .billingState(customer.getBillingState())
                .billingCountry(customer.getBillingCountry())
                .billingPostalCode(customer.getBillingPostalCode())
                .shippingAddress(customer.getShippingAddress())
                .shippingCity(customer.getShippingCity())
                .shippingState(customer.getShippingState())
                .shippingCountry(customer.getShippingCountry())
                .shippingPostalCode(customer.getShippingPostalCode())
                .creditLimit(customer.getCreditLimit())
                .paymentTerms(customer.getPaymentTerms())
                .customerType(customer.getCustomerType())
                .taxId(customer.getTaxId())
                .isActive(customer.getIsActive())
                // Audit fields — server-generated, never written by the client
                .createdAt(customer.getCreatedAt())
                .updatedAt(customer.getUpdatedAt())
                .createdBy(customer.getCreatedBy())
                .updatedBy(customer.getUpdatedBy())
                .version(customer.getVersion())
                .build();
    }

    /**
     * Convert a {@link CustomerCreateRequest} to a new Customer entity.
     * The entity has no ID set; persistence assigns it.
     *
     * @param request Creation payload (must not be null)
     * @return Customer entity ready to persist, or null if request is null
     */
    public Customer toEntity(CustomerCreateRequest request) {
        if (request == null) {
            return null;
        }

        return Customer.builder()
                .customerCode(request.getCustomerCode())
                .companyName(request.getCompanyName())
                .contactName(request.getContactName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .mobile(request.getMobile())
                .billingAddress(request.getBillingAddress())
                .billingCity(request.getBillingCity())
                .billingState(request.getBillingState())
                .billingCountry(request.getBillingCountry())
                .billingPostalCode(request.getBillingPostalCode())
                .shippingAddress(request.getShippingAddress())
                .shippingCity(request.getShippingCity())
                .shippingState(request.getShippingState())
                .shippingCountry(request.getShippingCountry())
                .shippingPostalCode(request.getShippingPostalCode())
                .creditLimit(request.getCreditLimit())
                .paymentTerms(request.getPaymentTerms())
                .customerType(request.getCustomerType())
                .taxId(request.getTaxId())
                // Default to active when caller omits the flag
                .isActive(request.getIsActive() != null ? request.getIsActive() : Boolean.TRUE)
                .build();
    }

    /**
     * Apply non-null fields from a {@link CustomerUpdateRequest} to an existing
     * Customer entity.
     * Null fields are left unchanged, making this method suitable for both PUT and
     * PATCH semantics.
     *
     * @param entity  Existing managed entity (must not be null)
     * @param request Update payload (must not be null)
     */
    public void applyUpdate(Customer entity, CustomerUpdateRequest request) {
        if (entity == null || request == null) {
            return;
        }

        // Identity
        if (request.getCompanyName() != null)
            entity.setCompanyName(request.getCompanyName());
        if (request.getContactName() != null)
            entity.setContactName(request.getContactName());
        if (request.getEmail() != null)
            entity.setEmail(request.getEmail());
        if (request.getPhone() != null)
            entity.setPhone(request.getPhone());
        if (request.getMobile() != null)
            entity.setMobile(request.getMobile());

        // Billing address
        if (request.getBillingAddress() != null)
            entity.setBillingAddress(request.getBillingAddress());
        if (request.getBillingCity() != null)
            entity.setBillingCity(request.getBillingCity());
        if (request.getBillingState() != null)
            entity.setBillingState(request.getBillingState());
        if (request.getBillingCountry() != null)
            entity.setBillingCountry(request.getBillingCountry());
        if (request.getBillingPostalCode() != null)
            entity.setBillingPostalCode(request.getBillingPostalCode());

        // Shipping address
        if (request.getShippingAddress() != null)
            entity.setShippingAddress(request.getShippingAddress());
        if (request.getShippingCity() != null)
            entity.setShippingCity(request.getShippingCity());
        if (request.getShippingState() != null)
            entity.setShippingState(request.getShippingState());
        if (request.getShippingCountry() != null)
            entity.setShippingCountry(request.getShippingCountry());
        if (request.getShippingPostalCode() != null)
            entity.setShippingPostalCode(request.getShippingPostalCode());

        // Commercial
        if (request.getCreditLimit() != null)
            entity.setCreditLimit(request.getCreditLimit());
        if (request.getPaymentTerms() != null)
            entity.setPaymentTerms(request.getPaymentTerms());
        if (request.getCustomerType() != null)
            entity.setCustomerType(request.getCustomerType());
        if (request.getTaxId() != null)
            entity.setTaxId(request.getTaxId());
        if (request.getIsActive() != null)
            entity.setIsActive(request.getIsActive());
    }
}