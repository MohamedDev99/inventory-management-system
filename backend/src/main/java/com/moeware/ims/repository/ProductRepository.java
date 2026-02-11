package com.moeware.ims.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.moeware.ims.entity.inventory.Product;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

        Optional<Product> findBySku(String sku);

        Optional<Product> findByBarcode(String barcode);

        Page<Product> findByIsActive(Boolean isActive, Pageable pageable);

        Page<Product> findByCategoryId(Long categoryId, Pageable pageable);

        Page<Product> findByIsActiveAndCategoryId(Boolean isActive, Long categoryId, Pageable pageable);

        @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
                        "OR LOWER(p.sku) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
        Page<Product> searchProducts(@Param("searchTerm") String searchTerm, Pageable pageable);

        @Query("SELECT p FROM Product p LEFT JOIN FETCH p.category WHERE p.id = :id")
        Optional<Product> findByIdWithCategory(@Param("id") Long id);

        @Query("SELECT p FROM Product p LEFT JOIN FETCH p.inventoryItems WHERE p.id = :id")
        Optional<Product> findByIdWithInventory(@Param("id") Long id);

        @Query("SELECT p FROM Product p " +
                        "LEFT JOIN p.inventoryItems i " +
                        "WHERE i.quantity <= p.reorderLevel " +
                        "AND p.isActive = true")
        List<Product> findLowStockProducts();

        @Query("SELECT p FROM Product p " +
                        "LEFT JOIN p.inventoryItems i " +
                        "WHERE i.quantity <= p.minStockLevel " +
                        "AND p.isActive = true")
        List<Product> findCriticalStockProducts();

        boolean existsBySku(String sku);

        boolean existsByBarcode(String barcode);

        long countByIsActive(Boolean isActive);

        long countByCategoryId(Long categoryId);
}