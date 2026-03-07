package com.moeware.ims.repository.transaction;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.moeware.ims.entity.transaction.PurchaseOrderItem;

/**
 * Repository for PurchaseOrderItem entity
 */
@Repository
public interface PurchaseOrderItemRepository extends JpaRepository<PurchaseOrderItem, Long> {

    List<PurchaseOrderItem> findByPurchaseOrderId(Long purchaseOrderId);

    Optional<PurchaseOrderItem> findByIdAndPurchaseOrderId(Long itemId, Long purchaseOrderId);

    boolean existsByProductId(Long productId);
}