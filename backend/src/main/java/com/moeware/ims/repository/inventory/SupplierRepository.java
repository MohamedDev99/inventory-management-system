package com.moeware.ims.repository.inventory;

import com.moeware.ims.entity.inventory.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for Supplier entity
 * Provides database access methods for supplier management
 */
@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    /**
     * Find supplier by code
     * 
     * @param code Unique supplier code
     * @return Optional containing supplier if found
     */
    Optional<Supplier> findByCode(String code);

    /**
     * Find supplier by email
     * 
     * @param email Supplier email
     * @return Optional containing supplier if found
     */
    Optional<Supplier> findByEmail(String email);

    /**
     * Check if supplier exists by code
     * 
     * @param code Supplier code
     * @return true if exists
     */
    boolean existsByCode(String code);

    /**
     * Check if supplier exists by email
     * 
     * @param email Supplier email
     * @return true if exists
     */
    boolean existsByEmail(String email);

    /**
     * Find suppliers by active status
     * 
     * @param isActive Active status
     * @param pageable Pagination information
     * @return Page of suppliers
     */
    Page<Supplier> findByIsActive(Boolean isActive, Pageable pageable);

    /**
     * Find suppliers by country
     * 
     * @param country  Country name
     * @param pageable Pagination information
     * @return Page of suppliers
     */
    Page<Supplier> findByCountry(String country, Pageable pageable);

    /**
     * Find suppliers by rating range
     * 
     * @param minRating Minimum rating
     * @param maxRating Maximum rating
     * @param pageable  Pagination information
     * @return Page of suppliers
     */
    Page<Supplier> findByRatingBetween(Integer minRating, Integer maxRating, Pageable pageable);

    /**
     * Search suppliers by name, code, or email
     * 
     * @param searchTerm Search term
     * @param pageable   Pagination information
     * @return Page of matching suppliers
     */
    @Query("SELECT s FROM Supplier s WHERE " +
            "LOWER(s.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(s.code) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(s.email) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Supplier> searchSuppliers(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Find active suppliers with filters
     * 
     * @param isActive   Active status (optional)
     * @param country    Country filter (optional)
     * @param minRating  Minimum rating (optional)
     * @param maxRating  Maximum rating (optional)
     * @param searchTerm Search term (optional)
     * @param pageable   Pagination information
     * @return Page of filtered suppliers
     */
    @Query("SELECT s FROM Supplier s WHERE " +
            "(:isActive IS NULL OR s.isActive = :isActive) AND " +
            "(:country IS NULL OR LOWER(s.country) = LOWER(:country)) AND " +
            "(:minRating IS NULL OR s.rating >= :minRating) AND " +
            "(:maxRating IS NULL OR s.rating <= :maxRating) AND " +
            "(:searchTerm IS NULL OR " +
            "LOWER(s.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(s.code) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(s.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Supplier> findSuppliersWithFilters(
            @Param("isActive") Boolean isActive,
            @Param("country") String country,
            @Param("minRating") Integer minRating,
            @Param("maxRating") Integer maxRating,
            @Param("searchTerm") String searchTerm,
            Pageable pageable);

    /**
     * Count active suppliers
     * 
     * @return Number of active suppliers
     */
    @Query("SELECT COUNT(s) FROM Supplier s WHERE s.isActive = true")
    Long countActiveSuppliers();

    /**
     * Get top-rated suppliers
     * 
     * @param pageable Pagination information
     * @return Page of top-rated suppliers
     */
    @Query("SELECT s FROM Supplier s WHERE s.isActive = true ORDER BY s.rating DESC")
    Page<Supplier> findTopRatedSuppliers(Pageable pageable);
}