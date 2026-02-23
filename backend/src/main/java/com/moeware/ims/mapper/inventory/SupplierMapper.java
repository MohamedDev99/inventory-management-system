package com.moeware.ims.mapper.inventory;

import org.springframework.stereotype.Component;

import com.moeware.ims.dto.inventory.supplier.SupplierDTO;
import com.moeware.ims.entity.inventory.Supplier;

/**
 * Mapper utility for converting between Supplier entity and DTO
 */
@Component
public class SupplierMapper {

    /**
     * Convert Supplier entity to DTO
     *
     * @param supplier Supplier entity
     * @return SupplierDTO
     */
    public SupplierDTO toDTO(Supplier supplier) {
        if (supplier == null) {
            return null;
        }

        return SupplierDTO.builder()
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
     * Convert SupplierDTO to entity
     *
     * @param dto SupplierDTO
     * @return Supplier entity
     */
    public Supplier toEntity(SupplierDTO dto) {
        if (dto == null) {
            return null;
        }

        return Supplier.builder()
                .id(dto.getId())
                .name(dto.getName())
                .code(dto.getCode())
                .contactPerson(dto.getContactPerson())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .address(dto.getAddress())
                .city(dto.getCity())
                .country(dto.getCountry())
                .paymentTerms(dto.getPaymentTerms())
                .rating(dto.getRating())
                .isActive(dto.getIsActive() != null ? dto.getIsActive() : true)
                .build();
    }

    /**
     * Update existing entity with DTO data
     *
     * @param entity Existing supplier entity
     * @param dto    SupplierDTO with updated data
     */
    public void updateEntityFromDTO(Supplier entity, SupplierDTO dto) {
        if (entity == null || dto == null) {
            return;
        }

        if (dto.getName() != null) {
            entity.setName(dto.getName());
        }
        if (dto.getCode() != null) {
            entity.setCode(dto.getCode());
        }
        if (dto.getContactPerson() != null) {
            entity.setContactPerson(dto.getContactPerson());
        }
        if (dto.getEmail() != null) {
            entity.setEmail(dto.getEmail());
        }
        if (dto.getPhone() != null) {
            entity.setPhone(dto.getPhone());
        }
        if (dto.getAddress() != null) {
            entity.setAddress(dto.getAddress());
        }
        if (dto.getCity() != null) {
            entity.setCity(dto.getCity());
        }
        if (dto.getCountry() != null) {
            entity.setCountry(dto.getCountry());
        }
        if (dto.getPaymentTerms() != null) {
            entity.setPaymentTerms(dto.getPaymentTerms());
        }
        if (dto.getRating() != null) {
            entity.setRating(dto.getRating());
        }
        if (dto.getIsActive() != null) {
            entity.setIsActive(dto.getIsActive());
        }
    }
}