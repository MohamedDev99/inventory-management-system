package com.moeware.ims.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.moeware.ims.entity.customer.Employee;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Optional<Employee> findByEmployeeCode(String employeeCode);

    Optional<Employee> findByEmail(String email);

    Optional<Employee> findByUserId(Long userId);

    Page<Employee> findByIsActive(Boolean isActive, Pageable pageable);

    Page<Employee> findByDepartmentId(Long departmentId, Pageable pageable);

    List<Employee> findByManagerId(Long managerId);

    @Query("SELECT e FROM Employee e WHERE LOWER(e.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "OR LOWER(e.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "OR LOWER(e.email) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Employee> searchEmployees(@Param("searchTerm") String searchTerm, Pageable pageable);

    @Query("SELECT e FROM Employee e LEFT JOIN FETCH e.department WHERE e.id = :id")
    Optional<Employee> findByIdWithDepartment(@Param("id") Long id);

    @Query("SELECT e FROM Employee e LEFT JOIN FETCH e.subordinates WHERE e.id = :id")
    Optional<Employee> findByIdWithSubordinates(@Param("id") Long id);

    boolean existsByEmployeeCode(String employeeCode);

    boolean existsByEmail(String email);

    long countByIsActive(Boolean isActive);

    long countByDepartmentId(Long departmentId);
}