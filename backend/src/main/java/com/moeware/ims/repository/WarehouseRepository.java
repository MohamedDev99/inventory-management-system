package com.moeware.ims.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.moeware.ims.entity.inventory.Warehouse;

import java.util.List;
import java.util.Optional;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {

    Optional<Warehouse> findByCode(String code);

    Optional<Warehouse> findByName(String name);

    List<Warehouse> findByIsActive(Boolean isActive);

    List<Warehouse> findByManagerId(Long managerId);

    @Query("SELECT w FROM Warehouse w LEFT JOIN FETCH w.inventoryItems WHERE w.id = :id")
    Optional<Warehouse> findByIdWithInventory(@Param("id") Long id);

    @Query("SELECT w FROM Warehouse w LEFT JOIN FETCH w.manager WHERE w.id = :id")
    Optional<Warehouse> findByIdWithManager(@Param("id") Long id);

    boolean existsByCode(String code);

    boolean existsByName(String name);

    long countByIsActive(Boolean isActive);
}