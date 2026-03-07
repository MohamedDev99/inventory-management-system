package com.moeware.ims.repository.transaction;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.moeware.ims.entity.transaction.SalesOrderItem;

/**
 * Repository for SalesOrderItem entity
 */
@Repository
public interface SalesOrderItemRepository extends JpaRepository<SalesOrderItem, Long> {

    List<SalesOrderItem> findBySalesOrderId(Long salesOrderId);

    Optional<SalesOrderItem> findByIdAndSalesOrderId(Long itemId, Long salesOrderId);

    boolean existsByProductId(Long productId);
}