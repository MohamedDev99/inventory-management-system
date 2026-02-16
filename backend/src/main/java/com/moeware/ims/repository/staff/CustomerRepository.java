package com.moeware.ims.repository.staff;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.moeware.ims.entity.staff.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByCustomerCode(String customerCode);

    Optional<Customer> findByEmail(String email);

    Page<Customer> findByIsActive(Boolean isActive, Pageable pageable);

    Page<Customer> findByCustomerType(String customerType, Pageable pageable);

    @Query("SELECT c FROM Customer c WHERE LOWER(c.contactName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) "
            + "OR LOWER(c.companyName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) "
            + "OR LOWER(c.email) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Customer> searchCustomers(@Param("searchTerm") String searchTerm, Pageable pageable);

    List<Customer> findByCustomerTypeAndIsActive(String customerType, Boolean isActive);

    boolean existsByCustomerCode(String customerCode);

    boolean existsByEmail(String email);

    long countByIsActive(Boolean isActive);

    long countByCustomerType(String customerType);
}
