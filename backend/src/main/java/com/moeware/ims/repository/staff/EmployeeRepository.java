package com.moeware.ims.repository.staff;

import com.moeware.ims.entity.staff.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for Employee entity
 *
 * @author MoeWare Team
 */
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    boolean existsByEmployeeCode(String employeeCode);

    boolean existsByEmail(String email);

    boolean existsByEmployeeCodeAndIdNot(String employeeCode, Long id);

    boolean existsByEmailAndIdNot(String email, Long id);

    Optional<Employee> findByEmployeeCode(String employeeCode);

    Optional<Employee> findByEmail(String email);

    boolean existsByUserId(Long userId);

    /**
     * Find all employees with optional filters and pagination.
     */
    @Query(value = """
            SELECT e FROM Employee e
            LEFT JOIN FETCH e.department
            LEFT JOIN FETCH e.manager
            LEFT JOIN FETCH e.user u
            LEFT JOIN FETCH u.role
            WHERE (:search IS NULL OR LOWER(e.firstName) LIKE LOWER(CONCAT('%', :search, '%'))
                                   OR LOWER(e.lastName) LIKE LOWER(CONCAT('%', :search, '%'))
                                   OR LOWER(e.email) LIKE LOWER(CONCAT('%', :search, '%'))
                                   OR LOWER(e.employeeCode) LIKE LOWER(CONCAT('%', :search, '%')))
              AND (:departmentId IS NULL OR e.department.id = :departmentId)
              AND (:managerId IS NULL OR e.manager.id = :managerId)
              AND (:isActive IS NULL OR e.isActive = :isActive)
            """, countQuery = """
            SELECT COUNT(e) FROM Employee e
            WHERE (:search IS NULL OR LOWER(e.firstName) LIKE LOWER(CONCAT('%', :search, '%'))
                                   OR LOWER(e.lastName) LIKE LOWER(CONCAT('%', :search, '%'))
                                   OR LOWER(e.email) LIKE LOWER(CONCAT('%', :search, '%'))
                                   OR LOWER(e.employeeCode) LIKE LOWER(CONCAT('%', :search, '%')))
              AND (:departmentId IS NULL OR e.department.id = :departmentId)
              AND (:managerId IS NULL OR e.manager.id = :managerId)
              AND (:isActive IS NULL OR e.isActive = :isActive)
            """)
    Page<Employee> findAllWithFilters(
            @Param("search") String search,
            @Param("departmentId") Long departmentId,
            @Param("managerId") Long managerId,
            @Param("isActive") Boolean isActive,
            Pageable pageable);

    /**
     * Find employee by ID with full details eagerly loaded.
     */
    @Query("""
            SELECT e FROM Employee e
            LEFT JOIN FETCH e.department
            LEFT JOIN FETCH e.manager
            LEFT JOIN FETCH e.subordinates
            LEFT JOIN FETCH e.user u
            LEFT JOIN FETCH u.role
            WHERE e.id = :id
            """)
    Optional<Employee> findByIdWithDetails(@Param("id") Long id);

    /**
     * Find direct subordinates of a manager (paginated).
     */
    @Query(value = """
            SELECT e FROM Employee e
            LEFT JOIN FETCH e.department
            LEFT JOIN FETCH e.user u
            LEFT JOIN FETCH u.role
            WHERE e.manager.id = :managerId
            """, countQuery = "SELECT COUNT(e) FROM Employee e WHERE e.manager.id = :managerId")
    Page<Employee> findSubordinatesByManagerId(@Param("managerId") Long managerId, Pageable pageable);
}