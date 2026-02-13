package com.moeware.ims.repository.staff;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.moeware.ims.entity.staff.Department;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    Optional<Department> findByDepartmentCode(String departmentCode);

    Optional<Department> findByName(String name);

    List<Department> findByIsActive(Boolean isActive);

    List<Department> findByParentDepartmentIsNull(); // Root departments

    List<Department> findByParentDepartment(Department parentDepartment);

    @Query("SELECT d FROM Department d LEFT JOIN FETCH d.employees WHERE d.id = :id")
    Optional<Department> findByIdWithEmployees(@Param("id") Long id);

    @Query("SELECT d FROM Department d LEFT JOIN FETCH d.childDepartments WHERE d.id = :id")
    Optional<Department> findByIdWithChildren(@Param("id") Long id);

    boolean existsByDepartmentCode(String departmentCode);

    boolean existsByName(String name);

    long countByIsActive(Boolean isActive);
}