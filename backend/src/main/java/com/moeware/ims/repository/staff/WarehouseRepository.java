package com.moeware.ims.repository.staff;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.moeware.ims.entity.staff.Warehouse;

/**
 * Repository interface for Warehouse entity operations.
 * Extends JpaRepository for basic CRUD operations and JpaSpecificationExecutor
 * for dynamic queries.
 */
@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Long>, JpaSpecificationExecutor<Warehouse> {

        /**
         * Find warehouse by code
         *
         * @param code the warehouse code
         * @return Optional containing the warehouse if found
         */
        Optional<Warehouse> findByCode(String code);

        /**
         * Find warehouse by name
         *
         * @param name the warehouse name
         * @return Optional containing the warehouse if found
         */
        Optional<Warehouse> findByName(String name);

        /**
         * Check if warehouse with given code exists
         *
         * @param code the warehouse code
         * @return true if exists, false otherwise
         */
        boolean existsByCode(String code);

        /**
         * Check if warehouse with given name exists
         *
         * @param name the warehouse name
         * @return true if exists, false otherwise
         */
        boolean existsByName(String name);

        /**
         * Find all active warehouses
         *
         * @param isActive active status
         * @param pageable pagination information
         * @return Page of warehouses
         */
        Page<Warehouse> findByIsActive(Boolean isActive, Pageable pageable);

        /**
         * Find warehouses by manager
         *
         * @param managerId the manager user ID
         * @param pageable  pagination information
         * @return Page of warehouses managed by user
         */
        Page<Warehouse> findByManagerId(Long managerId, Pageable pageable);

        /**
         * Find warehouses by city
         *
         * @param city     the city name
         * @param pageable pagination information
         * @return Page of warehouses in city
         */
        Page<Warehouse> findByCity(String city, Pageable pageable);

        /**
         * Find warehouses by country
         *
         * @param country  the country name
         * @param pageable pagination information
         * @return Page of warehouses in country
         */
        Page<Warehouse> findByCountry(String country, Pageable pageable);

        /**
         * Find warehouses by state
         *
         * @param state    the state/province name
         * @param pageable pagination information
         * @return Page of warehouses in state
         */
        Page<Warehouse> findByState(String state, Pageable pageable);

        /**
         * Search warehouses by name, code, or city
         *
         * @param searchTerm the search term
         * @param pageable   pagination information
         * @return Page of matching warehouses
         */
        @Query("SELECT w FROM Warehouse w WHERE " +
                        "LOWER(w.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
                        "LOWER(w.code) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
                        "LOWER(w.city) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
                        "LOWER(w.address) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
        Page<Warehouse> searchWarehouses(@Param("searchTerm") String searchTerm, Pageable pageable);

        /**
         * Find all active warehouses
         *
         * @return List of active warehouses
         */
        List<Warehouse> findAllByIsActive(Boolean isActive);

        /**
         * Count active warehouses
         *
         * @return count of active warehouses
         */
        long countByIsActive(Boolean isActive);

        /**
         * Find warehouses by city and active status
         *
         * @param city     the city name
         * @param isActive active status
         * @param pageable pagination information
         * @return Page of warehouses
         */
        Page<Warehouse> findByCityAndIsActive(String city, Boolean isActive, Pageable pageable);

        /**
         * Find warehouses by country and active status
         *
         * @param country  the country name
         * @param isActive active status
         * @param pageable pagination information
         * @return Page of warehouses
         */
        Page<Warehouse> findByCountryAndIsActive(String country, Boolean isActive, Pageable pageable);

        /**
         * Get warehouse statistics
         *
         * @param warehouseId the warehouse ID
         * @return statistics map
         */
        @Query("SELECT new map(" +
                        "COUNT(DISTINCT ii.product.id) as totalProducts, " +
                        "SUM(ii.quantity) as totalUnits, " +
                        "SUM(ii.quantity * ii.product.costPrice) as totalValue) " +
                        "FROM InventoryItem ii " +
                        "WHERE ii.warehouse.id = :warehouseId")
        Object getWarehouseStatistics(@Param("warehouseId") Long warehouseId);

        /**
         * Find warehouses with capacity greater than or equal to specified value
         *
         * @param minCapacity minimum capacity
         * @param pageable    pagination information
         * @return Page of warehouses
         */
        @Query("SELECT w FROM Warehouse w WHERE w.capacity >= :minCapacity AND w.isActive = true")
        Page<Warehouse> findByMinimumCapacity(@Param("minCapacity") BigDecimal minCapacity, Pageable pageable);

        /**
         * Get total number of inventory items in warehouse
         *
         * @param warehouseId the warehouse ID
         * @return count of inventory items
         */
        @Query("SELECT COUNT(ii) FROM InventoryItem ii WHERE ii.warehouse.id = :warehouseId")
        long getInventoryItemCount(@Param("warehouseId") Long warehouseId);

        /**
         * Get total stock units in warehouse
         *
         * @param warehouseId the warehouse ID
         * @return total quantity of all items
         */
        @Query("SELECT COALESCE(SUM(ii.quantity), 0) FROM InventoryItem ii WHERE ii.warehouse.id = :warehouseId")
        int getTotalStockUnits(@Param("warehouseId") Long warehouseId);

        /**
         * Find warehouses with low stock products
         *
         * @return List of warehouses with low stock alerts
         */
        @Query("SELECT DISTINCT w FROM Warehouse w " +
                        "JOIN w.inventoryItems ii " +
                        "WHERE ii.quantity <= ii.product.reorderLevel " +
                        "AND w.isActive = true")
        List<Warehouse> findWarehousesWithLowStock();
}