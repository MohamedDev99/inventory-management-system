package com.moeware.ims.repository.staff;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.moeware.ims.entity.staff.Customer;

/**
 * Repository interface for Customer entity
 * Provides database access methods for customer management
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    /**
     * Find customer by customer code
     *
     * @param customerCode Unique customer code
     * @return Optional containing customer if found
     */
    Optional<Customer> findByCustomerCode(String customerCode);

    /**
     * Find customer by email
     *
     * @param email Customer email
     * @return Optional containing customer if found
     */
    Optional<Customer> findByEmail(String email);

    /**
     * Check if customer exists by customer code
     *
     * @param customerCode Customer code
     * @return true if exists
     */
    boolean existsByCustomerCode(String customerCode);

    /**
     * Check if customer exists by email
     *
     * @param email Customer email
     * @return true if exists
     */
    boolean existsByEmail(String email);

    /**
     * Find customers by active status
     *
     * @param isActive Active status
     * @param pageable Pagination information
     * @return Page of customers
     */
    Page<Customer> findByIsActive(Boolean isActive, Pageable pageable);

    /**
     * Find customers by type
     *
     * @param customerType Customer type (RETAIL, WHOLESALE, CORPORATE)
     * @param pageable     Pagination information
     * @return Page of customers
     */
    Page<Customer> findByCustomerType(String customerType, Pageable pageable);

    /**
     * Find customers by city
     *
     * @param city     City name
     * @param pageable Pagination information
     * @return Page of customers
     */
    Page<Customer> findByBillingCity(String city, Pageable pageable);

    /**
     * Find customers by country
     *
     * @param country  Country name
     * @param pageable Pagination information
     * @return Page of customers
     */
    Page<Customer> findByBillingCountry(String country, Pageable pageable);

    /**
     * Search customers by name, code, email, or company
     *
     * @param searchTerm Search term
     * @param pageable   Pagination information
     * @return Page of matching customers
     */
    @Query("SELECT c FROM Customer c WHERE " +
            "LOWER(c.customerCode) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(c.contactName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(c.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(c.companyName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Customer> searchCustomers(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Find customers with filters
     *
     * @param customerType Customer type (optional)
     * @param isActive     Active status (optional)
     * @param city         City filter (optional)
     * @param country      Country filter (optional)
     * @param searchTerm   Search term (optional)
     * @param pageable     Pagination information
     * @return Page of filtered customers
     */
    @Query("SELECT c FROM Customer c WHERE " +
            "(:customerType IS NULL OR LOWER(c.customerType) = LOWER(:customerType)) AND " +
            "(:isActive IS NULL OR c.isActive = :isActive) AND " +
            "(:city IS NULL OR LOWER(c.billingCity) = LOWER(:city)) AND " +
            "(:country IS NULL OR LOWER(c.billingCountry) = LOWER(:country)) AND " +
            "(:searchTerm IS NULL OR " +
            "LOWER(c.customerCode) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(c.contactName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(c.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(c.companyName) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Customer> findCustomersWithFilters(
            @Param("customerType") String customerType,
            @Param("isActive") Boolean isActive,
            @Param("city") String city,
            @Param("country") String country,
            @Param("searchTerm") String searchTerm,
            Pageable pageable);

    /**
     * Count active customers
     *
     * @return Number of active customers
     */
    @Query("SELECT COUNT(c) FROM Customer c WHERE c.isActive = true")
    Long countActiveCustomers();

    /**
     * Count customers by type
     *
     * @param customerType Customer type
     * @return Number of customers
     */
    Long countByCustomerType(String customerType);

    /**
     * Find retail customers
     *
     * @param pageable Pagination information
     * @return Page of retail customers
     */
    @Query("SELECT c FROM Customer c WHERE c.customerType = 'RETAIL' AND c.isActive = true")
    Page<Customer> findRetailCustomers(Pageable pageable);

    /**
     * Find wholesale customers
     *
     * @param pageable Pagination information
     * @return Page of wholesale customers
     */
    @Query("SELECT c FROM Customer c WHERE c.customerType = 'WHOLESALE' AND c.isActive = true")
    Page<Customer> findWholesaleCustomers(Pageable pageable);

    /**
     * Find corporate customers
     *
     * @param pageable Pagination information
     * @return Page of corporate customers
     */
    @Query("SELECT c FROM Customer c WHERE c.customerType = 'CORPORATE' AND c.isActive = true")
    Page<Customer> findCorporateCustomers(Pageable pageable);
}