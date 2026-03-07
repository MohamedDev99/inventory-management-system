package com.moeware.ims.service.inventory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.moeware.ims.dto.inventory.supplier.SupplierDTO;
import com.moeware.ims.dto.inventory.supplier.SupplierPerformanceDTO;
import com.moeware.ims.dto.transaction.purchaseOrder.PurchaseOrderResponse;
import com.moeware.ims.entity.inventory.Supplier;
import com.moeware.ims.entity.transaction.PurchaseOrder;
import com.moeware.ims.enums.transaction.PurchaseOrderStatus;
import com.moeware.ims.exception.ResourceNotFoundException;
import com.moeware.ims.exception.inventory.supplier.SupplierAlreadyExistsException;
import com.moeware.ims.exception.inventory.supplier.SupplierNotFoundException;
import com.moeware.ims.mapper.inventory.SupplierMapper;
import com.moeware.ims.mapper.transaction.PurchaseOrderMapper;
import com.moeware.ims.repository.inventory.SupplierRepository;
import com.moeware.ims.repository.transaction.PurchaseOrderRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service class for Supplier management
 * Implements business logic for supplier operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class SupplierService {

    private final SupplierRepository supplierRepository;
    private final SupplierMapper supplierMapper;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final PurchaseOrderMapper purchaseOrderMapper;

    /**
     * Get all suppliers with pagination and filters
     *
     * @param isActive   Filter by active status
     * @param country    Filter by country
     * @param minRating  Minimum rating
     * @param maxRating  Maximum rating
     * @param searchTerm Search term
     * @param pageable   Pagination information
     * @return Page of SupplierDTOs
     */
    public Page<SupplierDTO> getAllSuppliers(
            Boolean isActive,
            String country,
            Integer minRating,
            Integer maxRating,
            String searchTerm,
            Pageable pageable) {

        log.debug("Fetching suppliers with filters - active: {}, country: {}, rating: {}-{}, search: {}",
                isActive, country, minRating, maxRating, searchTerm);

        Page<Supplier> suppliers = supplierRepository.findSuppliersWithFilters(
                isActive, country, minRating, maxRating, searchTerm, pageable);

        return suppliers.map(supplierMapper::toDTO);
    }

    /**
     * Get supplier by ID
     *
     * @param id Supplier ID
     * @return SupplierDTO
     * @throws ResourceNotFoundException if supplier not found
     */
    public SupplierDTO getSupplierById(Long id) {
        log.debug("Fetching supplier with ID: {}", id);

        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with ID: " + id));

        return supplierMapper.toDTO(supplier);
    }

    /**
     * Get supplier by code
     *
     * @param code Supplier code
     * @return SupplierDTO
     * @throws ResourceNotFoundException if supplier not found
     */
    public SupplierDTO getSupplierByCode(String code) {
        log.debug("Fetching supplier with code: {}", code);

        Supplier supplier = supplierRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with code: " + code));

        return supplierMapper.toDTO(supplier);
    }

    /**
     * Create a new supplier
     *
     * @param supplierDTO Supplier data
     * @return Created SupplierDTO
     * @throws SupplierAlreadyExistsException if code or email already exists
     */
    @Transactional
    public SupplierDTO createSupplier(SupplierDTO supplierDTO) {
        log.info("Creating new supplier with code: {}", supplierDTO.getCode());

        // Validate unique constraints
        if (supplierRepository.existsByCode(supplierDTO.getCode())) {
            throw new SupplierAlreadyExistsException("code", supplierDTO.getCode());
        }

        if (supplierRepository.existsByEmail(supplierDTO.getEmail())) {
            throw new SupplierAlreadyExistsException("email", supplierDTO.getEmail());
        }

        // Set default values
        if (supplierDTO.getIsActive() == null) {
            supplierDTO.setIsActive(true);
        }

        Supplier supplier = supplierMapper.toEntity(supplierDTO);
        Supplier savedSupplier = supplierRepository.save(supplier);

        log.info("Successfully created supplier with ID: {} and code: {}", savedSupplier.getId(),
                savedSupplier.getCode());

        return supplierMapper.toDTO(savedSupplier);
    }

    /**
     * Update an existing supplier (full update)
     *
     * @param id          Supplier ID
     * @param supplierDTO Updated supplier data
     * @return Updated SupplierDTO
     * @throws ResourceNotFoundException      if supplier not found
     * @throws SupplierAlreadyExistsException if code or email conflicts with
     *                                        another
     *                                        supplier
     */
    @Transactional
    public SupplierDTO updateSupplier(Long id, SupplierDTO supplierDTO) {
        log.info("Updating supplier with ID: {}", id);

        Supplier existingSupplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with ID: " + id));

        // Check for duplicate code (excluding current supplier)
        if (!existingSupplier.getCode().equals(supplierDTO.getCode()) &&
                supplierRepository.existsByCode(supplierDTO.getCode())) {
            throw new SupplierAlreadyExistsException("code", supplierDTO.getCode());
        }

        // Check for duplicate email (excluding current supplier)
        if (!existingSupplier.getEmail().equals(supplierDTO.getEmail()) &&
                supplierRepository.existsByEmail(supplierDTO.getEmail())) {
            throw new SupplierAlreadyExistsException("email", supplierDTO.getEmail());
        }

        // Update all fields
        supplierMapper.updateEntityFromDTO(existingSupplier, supplierDTO);
        Supplier updatedSupplier = supplierRepository.save(existingSupplier);

        log.info("Successfully updated supplier with ID: {}", id);

        return supplierMapper.toDTO(updatedSupplier);
    }

    /**
     * Partially update a supplier
     *
     * @param id          Supplier ID
     * @param supplierDTO Partial supplier data
     * @return Updated SupplierDTO
     * @throws ResourceNotFoundException if supplier not found
     */
    @Transactional
    public SupplierDTO patchSupplier(Long id, SupplierDTO supplierDTO) {
        log.info("Partially updating supplier with ID: {}", id);

        Supplier existingSupplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with ID: " + id));

        // Validate unique constraints if fields are being updated
        if (supplierDTO.getCode() != null && !existingSupplier.getCode().equals(supplierDTO.getCode())) {
            if (supplierRepository.existsByCode(supplierDTO.getCode())) {
                throw new SupplierAlreadyExistsException("code", supplierDTO.getCode());
            }
        }

        if (supplierDTO.getEmail() != null && !existingSupplier.getEmail().equals(supplierDTO.getEmail())) {
            if (supplierRepository.existsByEmail(supplierDTO.getEmail())) {
                throw new SupplierAlreadyExistsException("email", supplierDTO.getEmail());
            }
        }

        // Update only provided fields
        supplierMapper.updateEntityFromDTO(existingSupplier, supplierDTO);
        Supplier updatedSupplier = supplierRepository.save(existingSupplier);

        log.info("Successfully patched supplier with ID: {}", id);

        return supplierMapper.toDTO(updatedSupplier);
    }

    /**
     * Soft delete a supplier (deactivate)
     *
     * @param id Supplier ID
     * @throws ResourceNotFoundException if supplier not found
     */
    @Transactional
    public void deleteSupplier(Long id) {
        log.info("Soft deleting supplier with ID: {}", id);

        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with ID: " + id));

        // TODO: Check if supplier has pending purchase orders before deleting
        // This would require PurchaseOrderRepository to be implemented

        supplier.setIsActive(false);
        supplierRepository.save(supplier);

        log.info("Successfully deactivated supplier with ID: {}", id);
    }

    /**
     * Get active suppliers count
     *
     * @return Number of active suppliers
     */
    public Long getActiveSupplierCount() {
        return supplierRepository.countActiveSuppliers();
    }

    /**
     * Get top-rated suppliers
     *
     * @param pageable Pagination information
     * @return Page of top-rated suppliers
     */
    public Page<SupplierDTO> getTopRatedSuppliers(Pageable pageable) {
        log.debug("Fetching top-rated suppliers");

        Page<Supplier> suppliers = supplierRepository.findTopRatedSuppliers(pageable);
        return suppliers.map(supplierMapper::toDTO);
    }

    /**
     * Search suppliers by term
     *
     * @param searchTerm Search term
     * @param pageable   Pagination information
     * @return Page of matching suppliers
     */
    public Page<SupplierDTO> searchSuppliers(String searchTerm, Pageable pageable) {
        log.debug("Searching suppliers with term: {}", searchTerm);

        Page<Supplier> suppliers = supplierRepository.searchSuppliers(searchTerm, pageable);
        return suppliers.map(supplierMapper::toDTO);
    }

    /**
     * Get supplier's purchase orders with filters
     */
    public Page<PurchaseOrderResponse> getSupplierOrders(
            Long supplierId,
            PurchaseOrderStatus status,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable) {

        log.debug("Fetching purchase orders for supplier ID: {} with filters", supplierId);

        // Verify supplier exists
        if (!supplierRepository.existsById(supplierId)) {
            throw new SupplierNotFoundException(supplierId);
        }

        Page<PurchaseOrder> orders = purchaseOrderRepository.findBySupplierIdWithFilters(
                supplierId, status, startDate, endDate, pageable);

        return orders.map(purchaseOrderMapper::toDTO);
    }

    /**
     * Get supplier performance metrics
     */
    public SupplierPerformanceDTO getSupplierPerformance(Long supplierId) {
        log.debug("Calculating performance metrics for supplier ID: {}", supplierId);

        // Verify supplier exists and get details
        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new SupplierNotFoundException(supplierId));

        // Get all orders for this supplier
        Page<PurchaseOrder> allOrders = purchaseOrderRepository.findBySupplierIdWithFilters(
                supplierId, null, null, null, Pageable.unpaged());

        List<PurchaseOrder> orders = allOrders.getContent();

        // Calculate metrics
        SupplierPerformanceDTO.PerformanceMetrics metrics = calculateMetrics(orders);
        SupplierPerformanceDTO.PerformanceTrends trends = calculateTrends(orders, supplier);
        String recommendations = generateRecommendations(supplier, metrics, trends);

        return SupplierPerformanceDTO.builder()
                .supplierId(supplier.getId())
                .supplierName(supplier.getName())
                .supplierCode(supplier.getCode())
                .rating(supplier.getRating())
                .metrics(metrics)
                .trends(trends)
                .recommendations(recommendations)
                .build();
    }

    /**
     * Calculate performance metrics from orders
     */
    private SupplierPerformanceDTO.PerformanceMetrics calculateMetrics(List<PurchaseOrder> orders) {
        long totalOrders = orders.size();

        if (totalOrders == 0) {
            return SupplierPerformanceDTO.PerformanceMetrics.builder()
                    .totalOrders(0L)
                    .totalSpent(BigDecimal.ZERO)
                    .averageOrderValue(BigDecimal.ZERO)
                    .onTimeDeliveryRate(0.0)
                    .averageDeliveryDays(0.0)
                    .cancelledOrders(0L)
                    .cancelledOrderRate(0.0)
                    .qualityIssues(0L)
                    .qualityIssueRate(0.0)
                    .responseTime("N/A")
                    .pendingOrders(0L)
                    .completedOrders(0L)
                    .completionRate(0.0)
                    .build();
        }

        // Total spent
        BigDecimal totalSpent = orders.stream()
                .filter(po -> po.getStatus() == PurchaseOrderStatus.RECEIVED)
                .map(PurchaseOrder::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Average order value
        long receivedOrders = orders.stream()
                .filter(po -> po.getStatus() == PurchaseOrderStatus.RECEIVED)
                .count();
        BigDecimal averageOrderValue = receivedOrders > 0
                ? totalSpent.divide(BigDecimal.valueOf(receivedOrders), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        // On-time delivery rate
        long ordersWithDeliveryDates = orders.stream()
                .filter(po -> po.getExpectedDeliveryDate() != null && po.getActualDeliveryDate() != null)
                .count();

        long onTimeDeliveries = orders.stream()
                .filter(po -> po.getExpectedDeliveryDate() != null && po.getActualDeliveryDate() != null)
                .filter(po -> !po.getActualDeliveryDate().isAfter(po.getExpectedDeliveryDate()))
                .count();

        double onTimeDeliveryRate = ordersWithDeliveryDates > 0
                ? (onTimeDeliveries * 100.0) / ordersWithDeliveryDates
                : 0.0;

        // Average delivery days
        double averageDeliveryDays = orders.stream()
                .filter(po -> po.getOrderDate() != null && po.getActualDeliveryDate() != null)
                .mapToLong(po -> ChronoUnit.DAYS.between(po.getOrderDate(), po.getActualDeliveryDate()))
                .average()
                .orElse(0.0);

        // Cancelled orders
        long cancelledOrders = orders.stream()
                .filter(po -> po.getStatus() == PurchaseOrderStatus.CANCELLED)
                .count();

        double cancelledOrderRate = (cancelledOrders * 100.0) / totalOrders;

        // Pending and completed orders
        long pendingOrders = orders.stream()
                .filter(po -> po.getStatus() == PurchaseOrderStatus.SUBMITTED ||
                        po.getStatus() == PurchaseOrderStatus.APPROVED)
                .count();

        long completedOrders = orders.stream()
                .filter(po -> po.getStatus() == PurchaseOrderStatus.RECEIVED)
                .count();

        double completionRate = (completedOrders * 100.0) / totalOrders;

        // Quality issues (placeholder - would need additional data)
        long qualityIssues = 0L; // This would come from a quality tracking system
        double qualityIssueRate = totalOrders > 0 ? (qualityIssues * 100.0) / totalOrders : 0.0;

        return SupplierPerformanceDTO.PerformanceMetrics.builder()
                .totalOrders(totalOrders)
                .totalSpent(totalSpent)
                .averageOrderValue(averageOrderValue)
                .onTimeDeliveryRate(Math.round(onTimeDeliveryRate * 10.0) / 10.0)
                .averageDeliveryDays(Math.round(averageDeliveryDays * 10.0) / 10.0)
                .cancelledOrders(cancelledOrders)
                .cancelledOrderRate(Math.round(cancelledOrderRate * 10.0) / 10.0)
                .qualityIssues(qualityIssues)
                .qualityIssueRate(Math.round(qualityIssueRate * 10.0) / 10.0)
                .responseTime("N/A") // Placeholder - would need response time tracking
                .pendingOrders(pendingOrders)
                .completedOrders(completedOrders)
                .completionRate(Math.round(completionRate * 10.0) / 10.0)
                .build();
    }

    /**
     * Calculate performance trends
     */
    private SupplierPerformanceDTO.PerformanceTrends calculateTrends(List<PurchaseOrder> orders, Supplier supplier) {
        // Order frequency
        String orderFrequency = "N/A";
        if (orders.size() >= 2) {
            LocalDate firstOrder = orders.stream()
                    .map(PurchaseOrder::getOrderDate)
                    .filter(date -> date != null)
                    .min(LocalDate::compareTo)
                    .orElse(null);

            LocalDate lastOrder = orders.stream()
                    .map(PurchaseOrder::getOrderDate)
                    .filter(date -> date != null)
                    .max(LocalDate::compareTo)
                    .orElse(null);

            if (firstOrder != null && lastOrder != null) {
                long daysBetween = ChronoUnit.DAYS.between(firstOrder, lastOrder);
                if (daysBetween > 0) {
                    double ordersPerMonth = (orders.size() * 30.0) / daysBetween;
                    if (ordersPerMonth >= 4) {
                        orderFrequency = "Weekly";
                    } else if (ordersPerMonth >= 2) {
                        orderFrequency = "Bi-weekly";
                    } else if (ordersPerMonth >= 1) {
                        orderFrequency = "Monthly";
                    } else {
                        orderFrequency = "Quarterly";
                    }
                }
            }
        }

        // Spending trend (simplified - comparing recent vs older orders)
        String spendTrend = calculateSpendTrend(orders);

        // Quality trend (based on rating)
        String qualityTrend = supplier.getRating() != null && supplier.getRating() >= 4 ? "Stable"
                : "Needs Improvement";

        // Delivery trend
        String deliveryTrend = "Stable"; // Placeholder - would need historical comparison

        return SupplierPerformanceDTO.PerformanceTrends.builder()
                .orderFrequency(orderFrequency)
                .spendTrend(spendTrend)
                .qualityTrend(qualityTrend)
                .deliveryTrend(deliveryTrend)
                .build();
    }

    /**
     * Calculate spending trend
     */
    private String calculateSpendTrend(List<PurchaseOrder> orders) {
        if (orders.size() < 4) {
            return "Insufficient Data";
        }

        // Sort by date
        List<PurchaseOrder> sortedOrders = orders.stream()
                .filter(po -> po.getOrderDate() != null && po.getStatus() == PurchaseOrderStatus.RECEIVED)
                .sorted((a, b) -> a.getOrderDate().compareTo(b.getOrderDate()))
                .toList();

        if (sortedOrders.size() < 4) {
            return "Insufficient Data";
        }

        // Compare first half vs second half
        int midpoint = sortedOrders.size() / 2;
        BigDecimal firstHalfTotal = sortedOrders.subList(0, midpoint).stream()
                .map(PurchaseOrder::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal secondHalfTotal = sortedOrders.subList(midpoint, sortedOrders.size()).stream()
                .map(PurchaseOrder::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (secondHalfTotal.compareTo(firstHalfTotal.multiply(BigDecimal.valueOf(1.1))) > 0) {
            return "Increasing";
        } else if (secondHalfTotal.compareTo(firstHalfTotal.multiply(BigDecimal.valueOf(0.9))) < 0) {
            return "Decreasing";
        } else {
            return "Stable";
        }
    }

    /**
     * Generate recommendations based on performance
     */
    private String generateRecommendations(
            Supplier supplier,
            SupplierPerformanceDTO.PerformanceMetrics metrics,
            SupplierPerformanceDTO.PerformanceTrends trends) {

        StringBuilder recommendations = new StringBuilder();

        // Rating-based recommendations
        if (supplier.getRating() != null) {
            if (supplier.getRating() >= 5) {
                recommendations.append(
                        "Excellent supplier. Consider increasing order volume and establishing preferred supplier status. ");
            } else if (supplier.getRating() >= 4) {
                recommendations.append("Good supplier with reliable performance. Suitable for regular orders. ");
            } else if (supplier.getRating() >= 3) {
                recommendations.append(
                        "Average supplier. Monitor performance closely and consider alternatives for critical items. ");
            } else {
                recommendations.append("Below-average supplier. Consider finding alternative suppliers. ");
            }
        }

        // Delivery performance recommendations
        if (metrics.getOnTimeDeliveryRate() != null) {
            if (metrics.getOnTimeDeliveryRate() < 80) {
                recommendations.append(
                        "On-time delivery rate is below acceptable threshold. Discuss improvement plans with supplier. ");
            } else if (metrics.getOnTimeDeliveryRate() >= 95) {
                recommendations.append("Excellent delivery reliability. ");
            }
        }

        // Cancellation rate recommendations
        if (metrics.getCancelledOrderRate() != null && metrics.getCancelledOrderRate() > 5) {
            recommendations.append("High cancellation rate detected. Review ordering process and supplier capacity. ");
        }

        // Spending trend recommendations
        if ("Increasing".equals(trends.getSpendTrend())) {
            recommendations.append(
                    "Spending is increasing. Consider negotiating volume discounts or improved payment terms. ");
        }

        if (recommendations.length() == 0) {
            recommendations
                    .append("No specific recommendations at this time. Continue monitoring supplier performance.");
        }

        return recommendations.toString().trim();
    }
}