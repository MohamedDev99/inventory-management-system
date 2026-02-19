package com.moeware.ims.mapper.staff;

import org.springframework.stereotype.Component;

import com.moeware.ims.dto.staff.customer.CustomerDTO;
import com.moeware.ims.entity.staff.Customer;

/**
 * Mapper utility for converting between Customer entity and DTO
 */
@Component
public class CustomerMapper {

    /**
     * Convert Customer entity to DTO
     *
     * @param customer Customer entity
     * @return CustomerDTO
     */
    public CustomerDTO toDTO(Customer customer) {
        if (customer == null) {
            return null;
        }

        return CustomerDTO.builder()
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
                .createdAt(customer.getCreatedAt())
                .updatedAt(customer.getUpdatedAt())
                .createdBy(customer.getCreatedBy())
                .updatedBy(customer.getUpdatedBy())
                .version(customer.getVersion())
                .build();
    }

    /**
     * Convert CustomerDTO to entity
     *
     * @param dto CustomerDTO
     * @return Customer entity
     */
    public Customer toEntity(CustomerDTO dto) {
        if (dto == null) {
            return null;
        }

        return Customer.builder()
                .id(dto.getId())
                .customerCode(dto.getCustomerCode())
                .companyName(dto.getCompanyName())
                .contactName(dto.getContactName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .mobile(dto.getMobile())
                .billingAddress(dto.getBillingAddress())
                .billingCity(dto.getBillingCity())
                .billingState(dto.getBillingState())
                .billingCountry(dto.getBillingCountry())
                .billingPostalCode(dto.getBillingPostalCode())
                .shippingAddress(dto.getShippingAddress())
                .shippingCity(dto.getShippingCity())
                .shippingState(dto.getShippingState())
                .shippingCountry(dto.getShippingCountry())
                .shippingPostalCode(dto.getShippingPostalCode())
                .creditLimit(dto.getCreditLimit())
                .paymentTerms(dto.getPaymentTerms())
                .customerType(dto.getCustomerType())
                .taxId(dto.getTaxId())
                .isActive(dto.getIsActive() != null ? dto.getIsActive() : true)
                .build();
    }

    /**
     * Update existing entity with DTO data
     *
     * @param entity Existing customer entity
     * @param dto    CustomerDTO with updated data
     */
    public void updateEntityFromDTO(Customer entity, CustomerDTO dto) {
        if (entity == null || dto == null) {
            return;
        }

        if (dto.getCustomerCode() != null) {
            entity.setCustomerCode(dto.getCustomerCode());
        }
        if (dto.getCompanyName() != null) {
            entity.setCompanyName(dto.getCompanyName());
        }
        if (dto.getContactName() != null) {
            entity.setContactName(dto.getContactName());
        }
        if (dto.getEmail() != null) {
            entity.setEmail(dto.getEmail());
        }
        if (dto.getPhone() != null) {
            entity.setPhone(dto.getPhone());
        }
        if (dto.getMobile() != null) {
            entity.setMobile(dto.getMobile());
        }
        if (dto.getBillingAddress() != null) {
            entity.setBillingAddress(dto.getBillingAddress());
        }
        if (dto.getBillingCity() != null) {
            entity.setBillingCity(dto.getBillingCity());
        }
        if (dto.getBillingState() != null) {
            entity.setBillingState(dto.getBillingState());
        }
        if (dto.getBillingCountry() != null) {
            entity.setBillingCountry(dto.getBillingCountry());
        }
        if (dto.getBillingPostalCode() != null) {
            entity.setBillingPostalCode(dto.getBillingPostalCode());
        }
        if (dto.getShippingAddress() != null) {
            entity.setShippingAddress(dto.getShippingAddress());
        }
        if (dto.getShippingCity() != null) {
            entity.setShippingCity(dto.getShippingCity());
        }
        if (dto.getShippingState() != null) {
            entity.setShippingState(dto.getShippingState());
        }
        if (dto.getShippingCountry() != null) {
            entity.setShippingCountry(dto.getShippingCountry());
        }
        if (dto.getShippingPostalCode() != null) {
            entity.setShippingPostalCode(dto.getShippingPostalCode());
        }
        if (dto.getCreditLimit() != null) {
            entity.setCreditLimit(dto.getCreditLimit());
        }
        if (dto.getPaymentTerms() != null) {
            entity.setPaymentTerms(dto.getPaymentTerms());
        }
        if (dto.getCustomerType() != null) {
            entity.setCustomerType(dto.getCustomerType());
        }
        if (dto.getTaxId() != null) {
            entity.setTaxId(dto.getTaxId());
        }
        if (dto.getIsActive() != null) {
            entity.setIsActive(dto.getIsActive());
        }
    }
}