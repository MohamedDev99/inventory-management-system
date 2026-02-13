package com.moeware.ims.repository.inventory;

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

import com.moeware.ims.entity.inventory.Product;

/**
 * Repository interface for Product entity operations.
 * Extends JpaRepository for basic CRUD operations and JpaSpecificationExecutor
 * for dynamic queries.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

        /**
         * Find product by SKU (Stock Keeping Unit)
         *
         * @param sku the SKU to search for
         * @return Optional containing the product if found
         */
        Optional<Product> findBySku(String sku);

        /**
         * Find product by barcode
         *
         * @param barcode the barcode to search for
         * @return Optional containing the product if found
         */
        Optional<Product> findByBarcode(String barcode);

        /**
         * Check if a product with given SKU exists
         *
         * @param sku the SKU to check
         * @return true if exists, false otherwise
         */
        boolean existsBySku(String sku);

        /**
         * Check if a product with given barcode exists
         *
         * @param barcode the barcode to check
         * @return true if exists, false otherwise
         */
        boolean existsByBarcode(String barcode);

        /**
         * Find all products by category ID
         *
         * @param categoryId the category ID
         * @param pageable   pagination information
         * @return Page of products in the category
         */
        Page<Product> findByCategoryId(Long categoryId, Pageable pageable);

        /**
         * Find all active products
         *
         * @param isActive active status
         * @param pageable pagination information
         * @return Page of active products
         */
        Page<Product> findByIsActive(Boolean isActive, Pageable pageable);

        /**
         * Find products by category and active status
         *
         * @param categoryId the category ID
         * @param isActive   active status
         * @param pageable   pagination information
         * @return Page of filtered products
         */
        Page<Product> findByCategoryIdAndIsActive(Long categoryId, Boolean isActive, Pageable pageable);

        /**
         * Search products by name or SKU
         *
         * @param searchTerm the search term
         * @param pageable   pagination information
         * @return Page of matching products
         */
        @Query("SELECT p FROM Product p WHERE " +
                        "LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
                        "LOWER(p.sku) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
                        "LOWER(p.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
        Page<Product> searchProducts(@Param("searchTerm") String searchTerm, Pageable pageable);

        /**
         * Find products within a price range
         *
         * @param minPrice minimum price
         * @param maxPrice maximum price
         * @param pageable pagination information
         * @return Page of products within price range
         */
        @Query("SELECT p FROM Product p WHERE p.unitPrice BETWEEN :minPrice AND :maxPrice AND p.isActive = true")
        Page<Product> findByPriceRange(@Param("minPrice") BigDecimal minPrice,
                        @Param("maxPrice") BigDecimal maxPrice,
                        Pageable pageable);

        /**
         * Find low stock products across all warehouses
         *
         * @return List of products that are low on stock
         */
        @Query("SELECT DISTINCT p FROM Product p " +
                        "JOIN p.inventoryItems ii " +
                        "WHERE ii.quantity <= p.reorderLevel " +
                        "AND p.isActive = true")
        List<Product> findLowStockProducts();

        /**
         * Find products below minimum stock level
         *
         * @return List of products below minimum stock
         */
        @Query("SELECT DISTINCT p FROM Product p " +
                        "JOIN p.inventoryItems ii " +
                        "WHERE ii.quantity <= p.minStockLevel " +
                        "AND p.isActive = true")
        List<Product> findCriticalStockProducts();

        /**
         * Find out of stock products
         *
         * @return List of out of stock products
         */
        @Query("SELECT DISTINCT p FROM Product p " +
                        "JOIN p.inventoryItems ii " +
                        "WHERE ii.quantity = 0 " +
                        "AND p.isActive = true")
        List<Product> findOutOfStockProducts();

        /**
         * Count active products
         *
         * @return count of active products
         */
        long countByIsActive(Boolean isActive);

        /**
         * Count products in a category
         *
         * @param categoryId the category ID
         * @return count of products in category
         */
        long countByCategoryId(Long categoryId);

        /**
         * Find all products in multiple categories
         *
         * @param categoryIds list of category IDs
         * @param pageable    pagination information
         * @return Page of products
         */
        Page<Product> findByCategoryIdIn(List<Long> categoryIds, Pageable pageable);

        /**
         * Get total inventory value (cost price * quantity)
         *
         * @return total inventory value
         */
        @Query("SELECT SUM(p.costPrice * ii.quantity) FROM Product p " +
                        "JOIN p.inventoryItems ii " +
                        "WHERE p.isActive = true")
        BigDecimal getTotalInventoryValue();

        /**
         * Find products by warehouse
         *
         * @param warehouseId the warehouse ID
         * @param pageable    pagination information
         * @return Page of products in warehouse
         */
        @Query("SELECT DISTINCT p FROM Product p " +
                        "JOIN p.inventoryItems ii " +
                        "WHERE ii.warehouse.id = :warehouseId " +
                        "AND p.isActive = true")
        Page<Product> findByWarehouseId(@Param("warehouseId") Long warehouseId, Pageable pageable);
}