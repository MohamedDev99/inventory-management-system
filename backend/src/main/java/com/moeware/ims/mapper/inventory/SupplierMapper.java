package com.moeware.ims.mapper.inventory;

import org.springframework.stereotype.Component;

import com.moeware.ims.dto.inventory.supplier.SupplierPatchRequest;
import com.moeware.ims.dto.inventory.supplier.SupplierRequest;
import com.moeware.ims.dto.inventory.supplier.SupplierResponse;
import com.moeware.ims.entity.inventory.Supplier;

/**
 * Mapper utility for converting between Supplier entity and DTOs
 */
@Component
public class SupplierMapper {

    /**
     * Convert Supplier entity to SupplierResponse (used in GET endpoints)
     */
    public SupplierResponse toResponse(Supplier supplier) {
        if (supplier == null)
            return null;

        return SupplierResponse.builder()
                .id(supplier.getId())
                .name(supplier.getName())
                .code(supplier.getCode())
                .contactPerson(supplier.getContactPerson())
                .email(supplier.getEmail())
                .phone(supplier.getPhone())
                .address(supplier.getAddress())
                .city(supplier.getCity())
                .country(supplier.getCountry())
                .paymentTerms(supplier.getPaymentTerms())
                .rating(supplier.getRating())
                .isActive(supplier.getIsActive())
                .createdAt(supplier.getCreatedAt())
                .updatedAt(supplier.getUpdatedAt())
                .createdBy(supplier.getCreatedBy())
                .updatedBy(supplier.getUpdatedBy())
                .version(supplier.getVersion())
                .build();
    }

    /**
     * Convert SupplierRequest to a new Supplier entity (used in POST)
     */
    public Supplier toEntity(SupplierRequest request) {
        if (request == null)
            return null;

        return Supplier.builder()
                .name(request.getName())
                .code(request.getCode())
                .contactPerson(request.getContactPerson())
                .email(request.getEmail())
                .phone(request.getPhone())
                .address(request.getAddress())
                .city(request.getCity())
                .country(request.getCountry())
                .paymentTerms(request.getPaymentTerms())
                .rating(request.getRating())
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .build();
    }

    /**
     * Apply all fields from SupplierRequest onto an existing entity (used in PUT)
     */
    public void updateEntityFromRequest(Supplier entity, SupplierRequest request) {
        if (entity == null || request == null)
            return;

        entity.setName(request.getName());
        entity.setCode(request.getCode());
        entity.setContactPerson(request.getContactPerson());
        entity.setEmail(request.getEmail());
        entity.setPhone(request.getPhone());
        entity.setAddress(request.getAddress());
        entity.setCity(request.getCity());
        entity.setCountry(request.getCountry());
        entity.setPaymentTerms(request.getPaymentTerms());
        entity.setRating(request.getRating());
        entity.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
    }

    /**
     * Apply only non-null fields from SupplierPatchRequest onto an existing entity
     * (used in PATCH)
     */
    public void patchEntityFromRequest(Supplier entity, SupplierPatchRequest request) {
        if (entity == null || request == null)
            return;

        if (request.getName() != null)
            entity.setName(request.getName());
        if (request.getCode() != null)
            entity.setCode(request.getCode());
        if (request.getContactPerson() != null)
            entity.setContactPerson(request.getContactPerson());
        if (request.getEmail() != null)
            entity.setEmail(request.getEmail());
        if (request.getPhone() != null)
            entity.setPhone(request.getPhone());
        if (request.getAddress() != null)
            entity.setAddress(request.getAddress());
        if (request.getCity() != null)
            entity.setCity(request.getCity());
        if (request.getCountry() != null)
            entity.setCountry(request.getCountry());
        if (request.getPaymentTerms() != null)
            entity.setPaymentTerms(request.getPaymentTerms());
        if (request.getRating() != null)
            entity.setRating(request.getRating());
        if (request.getIsActive() != null)
            entity.setIsActive(request.getIsActive());
    }
}