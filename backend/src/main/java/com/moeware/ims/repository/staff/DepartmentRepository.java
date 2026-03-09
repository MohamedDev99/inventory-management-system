package com.moeware.ims.repository.staff;

import com.moeware.ims.entity.staff.Department;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Department entity
 *
 * @author MoeWare Team
 */
@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    boolean existsByDepartmentCode(String departmentCode);

    boolean existsByName(String name);

    boolean existsByDepartmentCodeAndIdNot(String departmentCode, Long id);

    boolean existsByNameAndIdNot(String name, Long id);

    Optional<Department> findByDepartmentCode(String departmentCode);

    /**
     * Find all departments with optional search (by name or code) and active status
     * filter.
     */
    @Query("""
            SELECT d FROM Department d
            LEFT JOIN FETCH d.manager
            LEFT JOIN FETCH d.parentDepartment
            WHERE (:search IS NULL OR LOWER(d.name) LIKE LOWER(CONCAT('%', :search, '%'))
                                   OR LOWER(d.departmentCode) LIKE LOWER(CONCAT('%', :search, '%')))
              AND (:parentId IS NULL OR d.parentDepartment.id = :parentId)
              AND (:isActive IS NULL OR d.isActive = :isActive)
            """)
    List<Department> findAllWithFilters(
            @Param("search") String search,
            @Param("parentId") Long parentId,
            @Param("isActive") Boolean isActive);

    /**
     * Find departments with pagination and filters.
     */
    @Query(value = """
            SELECT d FROM Department d
            LEFT JOIN FETCH d.manager
            LEFT JOIN FETCH d.parentDepartment
            WHERE (:search IS NULL OR LOWER(d.name) LIKE LOWER(CONCAT('%', :search, '%'))
                                   OR LOWER(d.departmentCode) LIKE LOWER(CONCAT('%', :search, '%')))
              AND (:parentId IS NULL OR d.parentDepartment.id = :parentId)
              AND (:isActive IS NULL OR d.isActive = :isActive)
            """, countQuery = """
            SELECT COUNT(d) FROM Department d
            WHERE (:search IS NULL OR LOWER(d.name) LIKE LOWER(CONCAT('%', :search, '%'))
                                   OR LOWER(d.departmentCode) LIKE LOWER(CONCAT('%', :search, '%')))
              AND (:parentId IS NULL OR d.parentDepartment.id = :parentId)
              AND (:isActive IS NULL OR d.isActive = :isActive)
            """)
    Page<Department> findAllWithFiltersPaged(
            @Param("search") String search,
            @Param("parentId") Long parentId,
            @Param("isActive") Boolean isActive,
            Pageable pageable);

    /**
     * Find department by ID with employees eagerly loaded.
     */
    @Query("""
            SELECT d FROM Department d
            LEFT JOIN FETCH d.manager
            LEFT JOIN FETCH d.parentDepartment
            LEFT JOIN FETCH d.employees
            LEFT JOIN FETCH d.childDepartments
            WHERE d.id = :id
            """)
    Optional<Department> findByIdWithDetails(@Param("id") Long id);

    /**
     * Fetch employees of a specific department (paginated).
     */
    @Query("""
            SELECT e FROM Department d JOIN d.employees e
            WHERE d.id = :departmentId
            """)
    Page<com.moeware.ims.entity.staff.Employee> findEmployeesByDepartmentId(
            @Param("departmentId") Long departmentId,
            Pageable pageable);

    /**
     * Check if any active employees are in this department.
     */
    @Query("""
            SELECT COUNT(e) > 0 FROM Department d JOIN d.employees e
            WHERE d.id = :departmentId AND e.isActive = true
            """)
    boolean hasActiveEmployees(@Param("departmentId") Long departmentId);
}