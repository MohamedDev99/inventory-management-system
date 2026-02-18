package com.moeware.ims.repository.inventory;

import com.moeware.ims.entity.inventory.InventoryItem;
import com.moeware.ims.entity.inventory.Product;
import com.moeware.ims.entity.staff.Warehouse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for InventoryItem entity
 * Provides CRUD operations and custom queries for inventory management
 */
@Repository
public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long> {

        /**
         * Find inventory item by product and warehouse
         */
        Optional<InventoryItem> findByProductAndWarehouse(Product product, Warehouse warehouse);

        /**
         * Find all inventory items for a specific warehouse
         */
        Page<InventoryItem> findByWarehouse(Warehouse warehouse, Pageable pageable);

        /**
         * Find all inventory items for a specific product across all warehouses
         */
        List<InventoryItem> findByProduct(Product product);

        /**
         * Find all low stock items (where quantity <= reorder level)
         */
        @Query("SELECT i FROM InventoryItem i WHERE i.quantity <= i.product.reorderLevel")
        Page<InventoryItem> findLowStockItems(Pageable pageable);

        /**
         * Find all low stock items in a specific warehouse
         */
        @Query("SELECT i FROM InventoryItem i WHERE i.warehouse = :warehouse AND i.quantity <= i.product.reorderLevel")
        Page<InventoryItem> findLowStockItemsByWarehouse(@Param("warehouse") Warehouse warehouse, Pageable pageable);

        /**
         * Search inventory items by product name or SKU
         */
        @Query("SELECT i FROM InventoryItem i WHERE " +
                        "LOWER(i.product.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
                        "LOWER(i.product.sku) LIKE LOWER(CONCAT('%', :search, '%'))")
        Page<InventoryItem> searchInventoryItems(@Param("search") String search, Pageable pageable);

        /**
         * Find all inventory items with filters
         */
        @Query("SELECT i FROM InventoryItem i WHERE " +
                        "(:warehouseId IS NULL OR i.warehouse.id = :warehouseId) AND " +
                        "(:productId IS NULL OR i.product.id = :productId) AND " +
                        "(:lowStock = false OR i.quantity <= i.product.reorderLevel)")
        Page<InventoryItem> findAllWithFilters(
                        @Param("warehouseId") Long warehouseId,
                        @Param("productId") Long productId,
                        @Param("lowStock") Boolean lowStock,
                        Pageable pageable);

        /**
         * Get total inventory count across all warehouses
         */
        @Query("SELECT COUNT(i) FROM InventoryItem i")
        Long countTotalInventoryItems();

        /**
         * Get total inventory value by warehouse
         */
        @Query("SELECT SUM(i.quantity * i.product.unitPrice) FROM InventoryItem i WHERE i.warehouse.id = :warehouseId")
        Double calculateTotalValueByWarehouse(@Param("warehouseId") Long warehouseId);

        /**
         * Get total inventory units by warehouse
         */
        @Query("SELECT SUM(i.quantity) FROM InventoryItem i WHERE i.warehouse.id = :warehouseId")
        Long getTotalUnitsByWarehouse(@Param("warehouseId") Long warehouseId);

        /**
         * Check if product exists in warehouse
         */
        boolean existsByProductIdAndWarehouseId(Long productId, Long warehouseId);
}