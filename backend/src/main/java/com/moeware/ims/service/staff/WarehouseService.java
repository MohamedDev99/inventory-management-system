package com.moeware.ims.service.staff;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.moeware.ims.dto.staff.warehouse.WarehouseCreateRequest;
import com.moeware.ims.dto.staff.warehouse.WarehouseResponse;
import com.moeware.ims.dto.staff.warehouse.WarehouseStatsResponse;
import com.moeware.ims.dto.staff.warehouse.WarehouseUpdateRequest;
import com.moeware.ims.entity.User;
import com.moeware.ims.entity.staff.Warehouse;
import com.moeware.ims.repository.UserRepository;
import com.moeware.ims.repository.staff.WarehouseRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service class for Warehouse business logic and operations.
 * Handles CRUD operations, search, filtering, and statistics for warehouses.
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final UserRepository userRepository;

    /**
     * Create a new warehouse
     *
     * @param request warehouse creation request
     * @return created warehouse response
     */
    @Transactional
    public WarehouseResponse createWarehouse(WarehouseCreateRequest request) {
        log.info("Creating new warehouse with code: {}", request.getCode());

        // Validate unique constraints
        if (warehouseRepository.existsByCode(request.getCode())) {
            throw new IllegalArgumentException("Warehouse with code '" + request.getCode() + "' already exists");
        }

        if (warehouseRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Warehouse with name '" + request.getName() + "' already exists");
        }

        // Validate manager if provided
        User manager = null;
        if (request.getManagerId() != null) {
            manager = userRepository.findById(request.getManagerId())
                    .orElseThrow(
                            () -> new EntityNotFoundException("User with ID " + request.getManagerId() + " not found"));
        }

        // Create warehouse entity
        Warehouse warehouse = Warehouse.builder()
                .name(request.getName())
                .code(request.getCode())
                .address(request.getAddress())
                .city(request.getCity())
                .state(request.getState())
                .country(request.getCountry())
                .postalCode(request.getPostalCode())
                .manager(manager)
                .capacity(request.getCapacity())
                .isActive(request.getIsActive())
                .build();

        Warehouse savedWarehouse = warehouseRepository.save(warehouse);
        log.info("Warehouse created successfully with ID: {}", savedWarehouse.getId());

        return mapToResponse(savedWarehouse);
    }

    /**
     * Get warehouse by ID
     *
     * @param id warehouse ID
     * @return warehouse response
     */
    public WarehouseResponse getWarehouseById(Long id) {
        log.debug("Fetching warehouse with ID: {}", id);
        Warehouse warehouse = findWarehouseById(id);
        return mapToResponse(warehouse);
    }

    /**
     * Get warehouse by code
     *
     * @param code warehouse code
     * @return warehouse response
     */
    public WarehouseResponse getWarehouseByCode(String code) {
        log.debug("Fetching warehouse with code: {}", code);
        Warehouse warehouse = warehouseRepository.findByCode(code)
                .orElseThrow(() -> new EntityNotFoundException("Warehouse with code '" + code + "' not found"));
        return mapToResponse(warehouse);
    }

    /**
     * Get all warehouses with pagination and filters
     *
     * @param pageable pagination information
     * @param search   search term
     * @param isActive active status filter
     * @param city     city filter
     * @param country  country filter
     * @param state    state filter
     * @return page of warehouses
     */
    public Page<WarehouseResponse> getAllWarehouses(Pageable pageable, String search, Boolean isActive,
            String city, String country, String state) {
        log.debug("Fetching warehouses with filters - search: {}, isActive: {}, city: {}, country: {}",
                search, isActive, city, country);

        Specification<Warehouse> spec = (root, query, cb) -> cb.conjunction();

        // Apply search filter
        if (search != null && !search.trim().isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.or(
                    cb.like(cb.lower(root.get("name")), "%" + search.toLowerCase() + "%"),
                    cb.like(cb.lower(root.get("code")), "%" + search.toLowerCase() + "%"),
                    cb.like(cb.lower(root.get("city")), "%" + search.toLowerCase() + "%"),
                    cb.like(cb.lower(root.get("address")), "%" + search.toLowerCase() + "%")));
        }

        // Apply active status filter
        if (isActive != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("isActive"), isActive));
        }

        // Apply city filter
        if (city != null && !city.trim().isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.equal(cb.lower(root.get("city")), city.toLowerCase()));
        }

        // Apply country filter
        if (country != null && !country.trim().isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.equal(cb.lower(root.get("country")), country.toLowerCase()));
        }

        // Apply state filter
        if (state != null && !state.trim().isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.equal(cb.lower(root.get("state")), state.toLowerCase()));
        }

        Page<Warehouse> warehouses = warehouseRepository.findAll(spec, pageable);
        return warehouses.map(this::mapToResponse);
    }

    /**
     * Get all active warehouses
     *
     * @return list of active warehouses
     */
    public List<WarehouseResponse> getAllActiveWarehouses() {
        log.debug("Fetching all active warehouses");
        List<Warehouse> warehouses = warehouseRepository.findAllByIsActive(true);
        return warehouses.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get warehouses by manager
     *
     * @param managerId manager user ID
     * @param pageable  pagination information
     * @return page of warehouses
     */
    public Page<WarehouseResponse> getWarehousesByManager(Long managerId, Pageable pageable) {
        log.debug("Fetching warehouses for manager ID: {}", managerId);

        // Validate manager exists
        if (!userRepository.existsById(managerId)) {
            throw new EntityNotFoundException("User with ID " + managerId + " not found");
        }

        Page<Warehouse> warehouses = warehouseRepository.findByManagerId(managerId, pageable);
        return warehouses.map(this::mapToResponse);
    }

    /**
     * Get warehouse statistics
     *
     * @param id warehouse ID
     * @return warehouse statistics
     */
    public WarehouseStatsResponse getWarehouseStatistics(Long id) {
        log.debug("Fetching statistics for warehouse ID: {}", id);

        Warehouse warehouse = findWarehouseById(id);

        int totalProductTypes = warehouse.getTotalProductTypes();
        int totalStockUnits = warehouse.getTotalStockUnits();

        // Calculate total inventory value
        BigDecimal totalValue = warehouse.getInventoryItems().stream()
                .map(item -> item.getProduct().getCostPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Count low stock and out of stock products
        long lowStockProducts = warehouse.getInventoryItems().stream()
                .filter(item -> item.getQuantity() <= item.getProduct().getReorderLevel())
                .count();

        long outOfStockProducts = warehouse.getInventoryItems().stream()
                .filter(item -> item.getQuantity() == 0)
                .count();

        // Calculate capacity utilization (if capacity is set)
        BigDecimal capacityUtilization = null;
        if (warehouse.getCapacity() != null && warehouse.getCapacity().compareTo(BigDecimal.ZERO) > 0) {
            // This is a simplified calculation - in real scenario, you'd have actual space
            // utilization data
            capacityUtilization = BigDecimal.valueOf(totalStockUnits)
                    .divide(warehouse.getCapacity(), 2, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        }

        return WarehouseStatsResponse.builder()
                .warehouseId(warehouse.getId())
                .warehouseName(warehouse.getName())
                .totalProducts(totalProductTypes)
                .totalUnits(totalStockUnits)
                .totalValue(totalValue)
                .lowStockProducts((int) lowStockProducts)
                .outOfStockProducts((int) outOfStockProducts)
                .capacity(warehouse.getCapacity())
                .capacityUtilization(capacityUtilization)
                .build();
    }

    /**
     * Update warehouse
     *
     * @param id      warehouse ID
     * @param request update request
     * @return updated warehouse response
     */
    @Transactional
    public WarehouseResponse updateWarehouse(Long id, WarehouseUpdateRequest request) {
        log.info("Updating warehouse with ID: {}", id);

        Warehouse warehouse = findWarehouseById(id);

        // Update fields if provided
        if (request.getName() != null) {
            // Check if name is being changed and if new name already exists
            if (!request.getName().equals(warehouse.getName()) &&
                    warehouseRepository.existsByName(request.getName())) {
                throw new IllegalArgumentException("Warehouse with name '" + request.getName() + "' already exists");
            }
            warehouse.setName(request.getName());
        }
        if (request.getAddress() != null) {
            warehouse.setAddress(request.getAddress());
        }
        if (request.getCity() != null) {
            warehouse.setCity(request.getCity());
        }
        if (request.getState() != null) {
            warehouse.setState(request.getState());
        }
        if (request.getCountry() != null) {
            warehouse.setCountry(request.getCountry());
        }
        if (request.getPostalCode() != null) {
            warehouse.setPostalCode(request.getPostalCode());
        }
        if (request.getManagerId() != null) {
            User manager = userRepository.findById(request.getManagerId())
                    .orElseThrow(
                            () -> new EntityNotFoundException("User with ID " + request.getManagerId() + " not found"));
            warehouse.setManager(manager);
        }
        if (request.getCapacity() != null) {
            warehouse.setCapacity(request.getCapacity());
        }
        if (request.getIsActive() != null) {
            warehouse.setIsActive(request.getIsActive());
        }

        Warehouse updatedWarehouse = warehouseRepository.save(warehouse);
        log.info("Warehouse updated successfully with ID: {}", updatedWarehouse.getId());

        return mapToResponse(updatedWarehouse);
    }

    /**
     * Soft delete warehouse (deactivate)
     *
     * @param id warehouse ID
     */
    @Transactional
    public void deleteWarehouse(Long id) {
        log.info("Soft deleting warehouse with ID: {}", id);

        Warehouse warehouse = findWarehouseById(id);
        warehouse.setIsActive(false);
        warehouseRepository.save(warehouse);

        log.info("Warehouse soft deleted successfully with ID: {}", id);
    }

    /**
     * Hard delete warehouse (permanent deletion)
     *
     * @param id warehouse ID
     */
    @Transactional
    public void hardDeleteWarehouse(Long id) {
        log.warn("Hard deleting warehouse with ID: {}", id);

        Warehouse warehouse = findWarehouseById(id);

        // Check if warehouse has inventory
        if (!warehouse.getInventoryItems().isEmpty()) {
            throw new IllegalStateException("Cannot delete warehouse with existing inventory. " +
                    "Please transfer or remove all inventory first.");
        }

        warehouseRepository.deleteById(id);
        log.info("Warehouse permanently deleted with ID: {}", id);
    }

    /**
     * Count warehouses by active status
     *
     * @param isActive active status
     * @return count of warehouses
     */
    public long countWarehouses(Boolean isActive) {
        if (isActive != null) {
            return warehouseRepository.countByIsActive(isActive);
        }
        return warehouseRepository.count();
    }

    /**
     * Get warehouses with low stock alerts
     *
     * @return list of warehouses with low stock products
     */
    public List<WarehouseResponse> getWarehousesWithLowStock() {
        log.debug("Fetching warehouses with low stock alerts");
        List<Warehouse> warehouses = warehouseRepository.findWarehousesWithLowStock();
        return warehouses.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Helper methods

    private Warehouse findWarehouseById(Long id) {
        return warehouseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Warehouse with ID " + id + " not found"));
    }

    private WarehouseResponse mapToResponse(Warehouse warehouse) {
        return WarehouseResponse.builder()
                .id(warehouse.getId())
                .name(warehouse.getName())
                .code(warehouse.getCode())
                .address(warehouse.getAddress())
                .city(warehouse.getCity())
                .state(warehouse.getState())
                .country(warehouse.getCountry())
                .postalCode(warehouse.getPostalCode())
                .fullAddress(warehouse.getFullAddress())
                .manager(mapToManagerSummary(warehouse.getManager()))
                .capacity(warehouse.getCapacity())
                .isActive(warehouse.getIsActive())
                .totalProductTypes(warehouse.getTotalProductTypes())
                .totalStockUnits(warehouse.getTotalStockUnits())
                .createdAt(warehouse.getCreatedAt())
                .updatedAt(warehouse.getUpdatedAt())
                .version(warehouse.getVersion())
                .build();
    }

    private WarehouseResponse.ManagerSummary mapToManagerSummary(User manager) {
        if (manager == null) {
            return null;
        }

        return WarehouseResponse.ManagerSummary.builder()
                .id(manager.getId())
                .username(manager.getUsername())
                .email(manager.getEmail())
                .build();
    }
}