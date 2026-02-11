package com.moeware.ims.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.moeware.ims.entity.inventory.InventoryItem;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long> {

        Optional<InventoryItem> findByProductIdAndWarehouseId(Long productId, Long warehouseId);

        List<InventoryItem> findByProductId(Long productId);

        List<InventoryItem> findByWarehouseId(Long warehouseId);

        @Query("SELECT i FROM InventoryItem i " +
                        "LEFT JOIN FETCH i.product p " +
                        "LEFT JOIN FETCH i.warehouse w " +
                        "WHERE i.warehouse.id = :warehouseId")
        List<InventoryItem> findByWarehouseIdWithDetails(@Param("warehouseId") Long warehouseId);

        @Query("SELECT i FROM InventoryItem i " +
                        "LEFT JOIN FETCH i.product p " +
                        "WHERE p.id = :productId")
        List<InventoryItem> findByProductIdWithDetails(@Param("productId") Long productId);

        @Query("SELECT i FROM InventoryItem i " +
                        "JOIN i.product p " +
                        "WHERE i.quantity <= p.reorderLevel")
        List<InventoryItem> findLowStockItems();

        @Query("SELECT i FROM InventoryItem i " +
                        "JOIN i.product p " +
                        "WHERE i.quantity <= p.minStockLevel")
        List<InventoryItem> findCriticalStockItems();

        @Query("SELECT i FROM InventoryItem i " +
                        "JOIN i.product p " +
                        "WHERE i.warehouse.id = :warehouseId " +
                        "AND i.quantity <= p.reorderLevel")
        List<InventoryItem> findLowStockItemsByWarehouse(@Param("warehouseId") Long warehouseId);

        @Query("SELECT SUM(i.quantity) FROM InventoryItem i WHERE i.product.id = :productId")
        Integer getTotalStockByProduct(@Param("productId") Long productId);

        @Query("SELECT COUNT(i) FROM InventoryItem i WHERE i.warehouse.id = :warehouseId")
        Long countByWarehouse(@Param("warehouseId") Long warehouseId);

        boolean existsByProductIdAndWarehouseId(Long productId, Long warehouseId);
}