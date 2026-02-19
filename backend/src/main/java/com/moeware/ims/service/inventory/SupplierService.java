package com.moeware.ims.service.inventory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.moeware.ims.dto.inventory.supplier.SupplierDTO;
import com.moeware.ims.entity.inventory.Supplier;
import com.moeware.ims.exception.ResourceNotFoundException;
import com.moeware.ims.exception.inventory.supplier.SupplierAlreadyExistsException;
import com.moeware.ims.mapper.inventory.SupplierMapper;
import com.moeware.ims.repository.inventory.SupplierRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service class for Supplier management
 * Implements business logic for supplier operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class SupplierService {

    private final SupplierRepository supplierRepository;
    private final SupplierMapper supplierMapper;

    /**
     * Get all suppliers with pagination and filters
     *
     * @param isActive   Filter by active status
     * @param country    Filter by country
     * @param minRating  Minimum rating
     * @param maxRating  Maximum rating
     * @param searchTerm Search term
     * @param pageable   Pagination information
     * @return Page of SupplierDTOs
     */
    public Page<SupplierDTO> getAllSuppliers(
            Boolean isActive,
            String country,
            Integer minRating,
            Integer maxRating,
            String searchTerm,
            Pageable pageable) {

        log.debug("Fetching suppliers with filters - active: {}, country: {}, rating: {}-{}, search: {}",
                isActive, country, minRating, maxRating, searchTerm);

        Page<Supplier> suppliers = supplierRepository.findSuppliersWithFilters(
                isActive, country, minRating, maxRating, searchTerm, pageable);

        return suppliers.map(supplierMapper::toDTO);
    }

    /**
     * Get supplier by ID
     *
     * @param id Supplier ID
     * @return SupplierDTO
     * @throws ResourceNotFoundException if supplier not found
     */
    public SupplierDTO getSupplierById(Long id) {
        log.debug("Fetching supplier with ID: {}", id);

        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with ID: " + id));

        return supplierMapper.toDTO(supplier);
    }

    /**
     * Get supplier by code
     *
     * @param code Supplier code
     * @return SupplierDTO
     * @throws ResourceNotFoundException if supplier not found
     */
    public SupplierDTO getSupplierByCode(String code) {
        log.debug("Fetching supplier with code: {}", code);

        Supplier supplier = supplierRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with code: " + code));

        return supplierMapper.toDTO(supplier);
    }

    /**
     * Create a new supplier
     *
     * @param supplierDTO Supplier data
     * @return Created SupplierDTO
     * @throws SupplierAlreadyExistsException if code or email already exists
     */
    @Transactional
    public SupplierDTO createSupplier(SupplierDTO supplierDTO) {
        log.info("Creating new supplier with code: {}", supplierDTO.getCode());

        // Validate unique constraints
        if (supplierRepository.existsByCode(supplierDTO.getCode())) {
            throw new SupplierAlreadyExistsException("code", supplierDTO.getCode());
        }

        if (supplierRepository.existsByEmail(supplierDTO.getEmail())) {
            throw new SupplierAlreadyExistsException("email", supplierDTO.getEmail());
        }

        // Set default values
        if (supplierDTO.getIsActive() == null) {
            supplierDTO.setIsActive(true);
        }

        Supplier supplier = supplierMapper.toEntity(supplierDTO);
        Supplier savedSupplier = supplierRepository.save(supplier);

        log.info("Successfully created supplier with ID: {} and code: {}", savedSupplier.getId(),
                savedSupplier.getCode());

        return supplierMapper.toDTO(savedSupplier);
    }

    /**
     * Update an existing supplier (full update)
     *
     * @param id          Supplier ID
     * @param supplierDTO Updated supplier data
     * @return Updated SupplierDTO
     * @throws ResourceNotFoundException      if supplier not found
     * @throws SupplierAlreadyExistsException if code or email conflicts with
     *                                        another
     *                                        supplier
     */
    @Transactional
    public SupplierDTO updateSupplier(Long id, SupplierDTO supplierDTO) {
        log.info("Updating supplier with ID: {}", id);

        Supplier existingSupplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with ID: " + id));

        // Check for duplicate code (excluding current supplier)
        if (!existingSupplier.getCode().equals(supplierDTO.getCode()) &&
                supplierRepository.existsByCode(supplierDTO.getCode())) {
            throw new SupplierAlreadyExistsException("code", supplierDTO.getCode());
        }

        // Check for duplicate email (excluding current supplier)
        if (!existingSupplier.getEmail().equals(supplierDTO.getEmail()) &&
                supplierRepository.existsByEmail(supplierDTO.getEmail())) {
            throw new SupplierAlreadyExistsException("email", supplierDTO.getEmail());
        }

        // Update all fields
        supplierMapper.updateEntityFromDTO(existingSupplier, supplierDTO);
        Supplier updatedSupplier = supplierRepository.save(existingSupplier);

        log.info("Successfully updated supplier with ID: {}", id);

        return supplierMapper.toDTO(updatedSupplier);
    }

    /**
     * Partially update a supplier
     *
     * @param id          Supplier ID
     * @param supplierDTO Partial supplier data
     * @return Updated SupplierDTO
     * @throws ResourceNotFoundException if supplier not found
     */
    @Transactional
    public SupplierDTO patchSupplier(Long id, SupplierDTO supplierDTO) {
        log.info("Partially updating supplier with ID: {}", id);

        Supplier existingSupplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with ID: " + id));

        // Validate unique constraints if fields are being updated
        if (supplierDTO.getCode() != null && !existingSupplier.getCode().equals(supplierDTO.getCode())) {
            if (supplierRepository.existsByCode(supplierDTO.getCode())) {
                throw new SupplierAlreadyExistsException("code", supplierDTO.getCode());
            }
        }

        if (supplierDTO.getEmail() != null && !existingSupplier.getEmail().equals(supplierDTO.getEmail())) {
            if (supplierRepository.existsByEmail(supplierDTO.getEmail())) {
                throw new SupplierAlreadyExistsException("email", supplierDTO.getEmail());
            }
        }

        // Update only provided fields
        supplierMapper.updateEntityFromDTO(existingSupplier, supplierDTO);
        Supplier updatedSupplier = supplierRepository.save(existingSupplier);

        log.info("Successfully patched supplier with ID: {}", id);

        return supplierMapper.toDTO(updatedSupplier);
    }

    /**
     * Soft delete a supplier (deactivate)
     *
     * @param id Supplier ID
     * @throws ResourceNotFoundException if supplier not found
     */
    @Transactional
    public void deleteSupplier(Long id) {
        log.info("Soft deleting supplier with ID: {}", id);

        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with ID: " + id));

        // TODO: Check if supplier has pending purchase orders before deleting
        // This would require PurchaseOrderRepository to be implemented

        supplier.setIsActive(false);
        supplierRepository.save(supplier);

        log.info("Successfully deactivated supplier with ID: {}", id);
    }

    /**
     * Get active suppliers count
     *
     * @return Number of active suppliers
     */
    public Long getActiveSupplierCount() {
        return supplierRepository.countActiveSuppliers();
    }

    /**
     * Get top-rated suppliers
     *
     * @param pageable Pagination information
     * @return Page of top-rated suppliers
     */
    public Page<SupplierDTO> getTopRatedSuppliers(Pageable pageable) {
        log.debug("Fetching top-rated suppliers");

        Page<Supplier> suppliers = supplierRepository.findTopRatedSuppliers(pageable);
        return suppliers.map(supplierMapper::toDTO);
    }

    /**
     * Search suppliers by term
     *
     * @param searchTerm Search term
     * @param pageable   Pagination information
     * @return Page of matching suppliers
     */
    public Page<SupplierDTO> searchSuppliers(String searchTerm, Pageable pageable) {
        log.debug("Searching suppliers with term: {}", searchTerm);

        Page<Supplier> suppliers = supplierRepository.searchSuppliers(searchTerm, pageable);
        return suppliers.map(supplierMapper::toDTO);
    }
}