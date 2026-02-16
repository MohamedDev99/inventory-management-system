package com.moeware.ims.service.inventory;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.moeware.ims.dto.inventory.product.ProductCreateRequest;
import com.moeware.ims.dto.inventory.product.ProductResponse;
import com.moeware.ims.dto.inventory.product.ProductUpdateRequest;
import com.moeware.ims.entity.inventory.Category;
import com.moeware.ims.entity.inventory.Product;
import com.moeware.ims.repository.inventory.CategoryRepository;
import com.moeware.ims.repository.inventory.ProductRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service class for Product business logic and operations.
 * Handles CRUD operations, search, filtering, and business rules for products.
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    /**
     * Create a new product
     *
     * @param request product creation request
     * @return created product response
     */
    @Transactional
    public ProductResponse createProduct(ProductCreateRequest request) {
        log.info("Creating new product with SKU: {}", request.getSku());

        // Validate unique constraints
        if (productRepository.existsBySku(request.getSku())) {
            throw new IllegalArgumentException("Product with SKU '" + request.getSku() + "' already exists");
        }

        if (request.getBarcode() != null && productRepository.existsByBarcode(request.getBarcode())) {
            throw new IllegalArgumentException("Product with barcode '" + request.getBarcode() + "' already exists");
        }

        // Validate category exists
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Category with ID " + request.getCategoryId() + " not found"));

        // Create product entity
        Product product = Product.builder()
                .sku(request.getSku())
                .name(request.getName())
                .description(request.getDescription())
                .category(category)
                .unit(request.getUnit())
                .unitPrice(request.getUnitPrice())
                .costPrice(request.getCostPrice())
                .reorderLevel(request.getReorderLevel())
                .minStockLevel(request.getMinStockLevel())
                .barcode(request.getBarcode())
                .imageUrl(request.getImageUrl())
                .isActive(request.getIsActive())
                .build();

        Product savedProduct = productRepository.save(product);
        log.info("Product created successfully with ID: {}", savedProduct.getId());

        return mapToResponse(savedProduct);
    }

    /**
     * Get product by ID
     *
     * @param id product ID
     * @return product response
     */
    public ProductResponse getProductById(Long id) {
        log.debug("Fetching product with ID: {}", id);
        Product product = findProductById(id);
        return mapToResponse(product);
    }

    /**
     * Get product by SKU
     *
     * @param sku product SKU
     * @return product response
     */
    public ProductResponse getProductBySku(String sku) {
        log.debug("Fetching product with SKU: {}", sku);
        Product product = productRepository.findBySku(sku)
                .orElseThrow(() -> new EntityNotFoundException("Product with SKU '" + sku + "' not found"));
        return mapToResponse(product);
    }

    /**
     * Get product by barcode
     *
     * @param barcode product barcode
     * @return product response
     */
    public ProductResponse getProductByBarcode(String barcode) {
        log.debug("Fetching product with barcode: {}", barcode);
        Product product = productRepository.findByBarcode(barcode)
                .orElseThrow(() -> new EntityNotFoundException("Product with barcode '" + barcode + "' not found"));
        return mapToResponse(product);
    }

    /**
     * Get all products with pagination and filters
     *
     * @param pageable   pagination information
     * @param search     search term
     * @param categoryId category filter
     * @param isActive   active status filter
     * @param minPrice   minimum price filter
     * @param maxPrice   maximum price filter
     * @return page of products
     */
    public Page<ProductResponse> getAllProducts(Pageable pageable, String search, Long categoryId,
            Boolean isActive, BigDecimal minPrice, BigDecimal maxPrice) {
        log.debug("Fetching products with filters - search: {}, categoryId: {}, isActive: {}",
                search, categoryId, isActive);

        Specification<Product> spec = (root, query, cb) -> cb.conjunction();

        // Apply search filter
        if (search != null && !search.trim().isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.or(
                    cb.like(cb.lower(root.get("name")), "%" + search.toLowerCase() + "%"),
                    cb.like(cb.lower(root.get("sku")), "%" + search.toLowerCase() + "%"),
                    cb.like(cb.lower(root.get("description")), "%" + search.toLowerCase() + "%")));
        }

        // Apply category filter
        if (categoryId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("category").get("id"), categoryId));
        }

        // Apply active status filter
        if (isActive != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("isActive"), isActive));
        }

        // Apply price range filter
        if (minPrice != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("unitPrice"), minPrice));
        }
        if (maxPrice != null) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("unitPrice"), maxPrice));
        }

        Page<Product> products = productRepository.findAll(spec, pageable);
        return products.map(this::mapToResponse);
    }

    /**
     * Get products by category
     *
     * @param categoryId category ID
     * @param pageable   pagination information
     * @return page of products
     */
    public Page<ProductResponse> getProductsByCategory(Long categoryId, Pageable pageable) {
        log.debug("Fetching products for category ID: {}", categoryId);

        // Validate category exists
        if (!categoryRepository.existsById(categoryId)) {
            throw new EntityNotFoundException("Category with ID " + categoryId + " not found");
        }

        Page<Product> products = productRepository.findByCategoryId(categoryId, pageable);
        return products.map(this::mapToResponse);
    }

    /**
     * Get low stock products
     *
     * @return list of low stock products
     */
    public List<ProductResponse> getLowStockProducts() {
        log.debug("Fetching low stock products");
        List<Product> products = productRepository.findLowStockProducts();
        return products.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get critical stock products (below minimum level)
     *
     * @return list of critical stock products
     */
    public List<ProductResponse> getCriticalStockProducts() {
        log.debug("Fetching critical stock products");
        List<Product> products = productRepository.findCriticalStockProducts();
        return products.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get out of stock products
     *
     * @return list of out of stock products
     */
    public List<ProductResponse> getOutOfStockProducts() {
        log.debug("Fetching out of stock products");
        List<Product> products = productRepository.findOutOfStockProducts();
        return products.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Update product
     *
     * @param id      product ID
     * @param request update request
     * @return updated product response
     */
    @Transactional
    public ProductResponse updateProduct(Long id, ProductUpdateRequest request) {
        log.info("Updating product with ID: {}", id);

        Product product = findProductById(id);

        // Update fields if provided
        if (request.getName() != null) {
            product.setName(request.getName());
        }
        if (request.getDescription() != null) {
            product.setDescription(request.getDescription());
        }
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Category with ID " + request.getCategoryId() + " not found"));
            product.setCategory(category);
        }
        if (request.getUnit() != null) {
            product.setUnit(request.getUnit());
        }
        if (request.getUnitPrice() != null) {
            product.setUnitPrice(request.getUnitPrice());
        }
        if (request.getCostPrice() != null) {
            product.setCostPrice(request.getCostPrice());
        }
        if (request.getReorderLevel() != null) {
            product.setReorderLevel(request.getReorderLevel());
        }
        if (request.getMinStockLevel() != null) {
            product.setMinStockLevel(request.getMinStockLevel());
        }
        if (request.getBarcode() != null) {
            // Check if barcode is being changed and if new barcode already exists
            if (!request.getBarcode().equals(product.getBarcode()) &&
                    productRepository.existsByBarcode(request.getBarcode())) {
                throw new IllegalArgumentException(
                        "Product with barcode '" + request.getBarcode() + "' already exists");
            }
            product.setBarcode(request.getBarcode());
        }
        if (request.getImageUrl() != null) {
            product.setImageUrl(request.getImageUrl());
        }
        if (request.getIsActive() != null) {
            product.setIsActive(request.getIsActive());
        }

        Product updatedProduct = productRepository.save(product);
        log.info("Product updated successfully with ID: {}", updatedProduct.getId());

        return mapToResponse(updatedProduct);
    }

    /**
     * Soft delete product (deactivate)
     *
     * @param id product ID
     */
    @Transactional
    public void deleteProduct(Long id) {
        log.info("Soft deleting product with ID: {}", id);

        Product product = findProductById(id);
        product.setIsActive(false);
        productRepository.save(product);

        log.info("Product soft deleted successfully with ID: {}", id);
    }

    /**
     * Hard delete product (permanent deletion)
     *
     * @param id product ID
     */
    @Transactional
    public void hardDeleteProduct(Long id) {
        log.warn("Hard deleting product with ID: {}", id);

        if (!productRepository.existsById(id)) {
            throw new EntityNotFoundException("Product with ID " + id + " not found");
        }

        productRepository.deleteById(id);
        log.info("Product permanently deleted with ID: {}", id);
    }

    /**
     * Get total inventory value
     *
     * @return total inventory value
     */
    public BigDecimal getTotalInventoryValue() {
        log.debug("Calculating total inventory value");
        BigDecimal value = productRepository.getTotalInventoryValue();
        return value != null ? value : BigDecimal.ZERO;
    }

    /**
     * Count products by active status
     *
     * @param isActive active status
     * @return count of products
     */
    public long countProducts(Boolean isActive) {
        if (isActive != null) {
            return productRepository.countByIsActive(isActive);
        }
        return productRepository.count();
    }

    // Helper methods

    private Product findProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product with ID " + id + " not found"));
    }

    private ProductResponse mapToResponse(Product product) {
        // Calculate total stock across all warehouses
        Integer totalStock = product.getInventoryItems().stream()
                .mapToInt(item -> item.getQuantity())
                .sum();

        return ProductResponse.builder()
                .id(product.getId())
                .sku(product.getSku())
                .name(product.getName())
                .description(product.getDescription())
                .category(mapToCategorySummary(product.getCategory()))
                .unit(product.getUnit())
                .unitPrice(product.getUnitPrice())
                .costPrice(product.getCostPrice())
                .profitMargin(product.getProfitMargin())
                .reorderLevel(product.getReorderLevel())
                .minStockLevel(product.getMinStockLevel())
                .barcode(product.getBarcode())
                .imageUrl(product.getImageUrl())
                .isActive(product.getIsActive())
                .totalStock(totalStock)
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .version(product.getVersion())
                .build();
    }

    private ProductResponse.CategorySummary mapToCategorySummary(Category category) {
        if (category == null) {
            return null;
        }

        return ProductResponse.CategorySummary.builder()
                .id(category.getId())
                .name(category.getName())
                .code(category.getCode())
                .fullPath(category.getFullPath())
                .build();
    }
}