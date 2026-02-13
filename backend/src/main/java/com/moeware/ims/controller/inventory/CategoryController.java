package com.moeware.ims.controller.inventory;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.moeware.ims.dto.inventory.category.CategoryCreateRequest;
import com.moeware.ims.dto.inventory.category.CategoryListItem;
import com.moeware.ims.dto.inventory.category.CategoryResponse;
import com.moeware.ims.dto.inventory.category.CategoryTreeNode;
import com.moeware.ims.dto.inventory.category.CategoryUpdateRequest;
import com.moeware.ims.service.inventory.CategoryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST Controller for Category management endpoints.
 * Provides CRUD operations for hierarchical category structure.
 */
@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Categories", description = "Category management APIs - Hierarchical category structure with parent-child relationships")
public class CategoryController {

        private final CategoryService categoryService;

        @PostMapping
        @Operation(summary = "Create a new category", description = "Creates a new category in the hierarchy. Can be a root category or a child of an existing category")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Category created successfully", content = @Content(schema = @Schema(implementation = CategoryResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Invalid input data or duplicate code/name"),
                        @ApiResponse(responseCode = "404", description = "Parent category not found")
        })
        public ResponseEntity<CategoryResponse> createCategory(
                        @Valid @RequestBody CategoryCreateRequest request) {
                log.info("REST request to create category: {}", request.getCode());
                CategoryResponse response = categoryService.createCategory(request);
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }

        @GetMapping("/{id}")
        @Operation(summary = "Get category by ID", description = "Retrieves detailed information about a category by its ID, including parent and child counts")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Category found", content = @Content(schema = @Schema(implementation = CategoryResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Category not found")
        })
        public ResponseEntity<CategoryResponse> getCategoryById(
                        @Parameter(description = "Category ID", required = true) @PathVariable Long id) {
                log.debug("REST request to get category by ID: {}", id);
                CategoryResponse response = categoryService.getCategoryById(id);
                return ResponseEntity.ok(response);
        }

        @GetMapping("/code/{code}")
        @Operation(summary = "Get category by code", description = "Retrieves a category by its unique code")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Category found", content = @Content(schema = @Schema(implementation = CategoryResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Category not found")
        })
        public ResponseEntity<CategoryResponse> getCategoryByCode(
                        @Parameter(description = "Category code", required = true, example = "ELEC") @PathVariable String code) {
                log.debug("REST request to get category by code: {}", code);
                CategoryResponse response = categoryService.getCategoryByCode(code);
                return ResponseEntity.ok(response);
        }

        @GetMapping
        @Operation(summary = "Get all categories with pagination and filters", description = "Retrieves a paginated list of categories with optional filters for search, parent category, and hierarchy level")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Categories retrieved successfully")
        })
        public ResponseEntity<Page<CategoryResponse>> getAllCategories(
                        @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) @Parameter(description = "Pagination information (page, size, sort)") Pageable pageable,

                        @Parameter(description = "Search term for name, code, or description", example = "Electronics") @RequestParam(required = false) String search,

                        @Parameter(description = "Filter by parent category ID (use 0 for root categories)", example = "1") @RequestParam(required = false) Long parentId,

                        @Parameter(description = "Filter by hierarchy level (0 for root)", example = "0") @RequestParam(required = false) Integer level) {

                log.debug("REST request to get all categories with filters");
                Page<CategoryResponse> categories = categoryService.getAllCategories(
                                pageable, search, parentId, level);
                return ResponseEntity.ok(categories);
        }

        @GetMapping("/root")
        @Operation(summary = "Get root categories", description = "Retrieves all top-level categories (categories without a parent)")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Root categories retrieved successfully")
        })
        public ResponseEntity<Page<CategoryResponse>> getRootCategories(
                        @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {

                log.debug("REST request to get root categories");
                Page<CategoryResponse> categories = categoryService.getRootCategories(pageable);
                return ResponseEntity.ok(categories);
        }

        @GetMapping("/{parentId}/children")
        @Operation(summary = "Get child categories", description = "Retrieves all direct child categories of a parent category")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Child categories retrieved successfully"),
                        @ApiResponse(responseCode = "404", description = "Parent category not found")
        })
        public ResponseEntity<Page<CategoryResponse>> getChildCategories(
                        @Parameter(description = "Parent category ID", required = true) @PathVariable Long parentId,

                        @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {

                log.debug("REST request to get child categories for parent: {}", parentId);
                Page<CategoryResponse> categories = categoryService.getChildCategories(parentId, pageable);
                return ResponseEntity.ok(categories);
        }

        @GetMapping("/tree")
        @Operation(summary = "Get complete category tree", description = "Retrieves the entire category hierarchy as a tree structure with nested children")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Category tree retrieved successfully")
        })
        public ResponseEntity<List<CategoryTreeNode>> getCategoryTree() {
                log.debug("REST request to get complete category tree");
                List<CategoryTreeNode> tree = categoryService.getCategoryTree();
                return ResponseEntity.ok(tree);
        }

        @GetMapping("/{id}/subtree")
        @Operation(summary = "Get category subtree", description = "Retrieves a subtree starting from a specific category, including all its descendants")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Category subtree retrieved successfully", content = @Content(schema = @Schema(implementation = CategoryTreeNode.class))),
                        @ApiResponse(responseCode = "404", description = "Category not found")
        })
        public ResponseEntity<CategoryTreeNode> getCategorySubtree(
                        @Parameter(description = "Category ID", required = true) @PathVariable Long id) {

                log.debug("REST request to get subtree for category: {}", id);
                CategoryTreeNode subtree = categoryService.getCategorySubtree(id);
                return ResponseEntity.ok(subtree);
        }

        @GetMapping("/list")
        @Operation(summary = "Get category list", description = "Retrieves all categories as a flat list (useful for dropdowns/selectors) ordered by hierarchy")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Category list retrieved successfully")
        })
        public ResponseEntity<List<CategoryListItem>> getCategoryList() {
                log.debug("REST request to get category list");
                List<CategoryListItem> categories = categoryService.getCategoryList();
                return ResponseEntity.ok(categories);
        }

        @GetMapping("/{id}/products")
        @Operation(summary = "Get product count in category tree", description = "Returns the total number of products in a category and all its subcategories")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Product count retrieved successfully"),
                        @ApiResponse(responseCode = "404", description = "Category not found")
        })
        public ResponseEntity<Long> getProductCountInCategoryTree(
                        @Parameter(description = "Category ID", required = true) @PathVariable Long id) {

                log.debug("REST request to get product count for category tree: {}", id);
                long count = categoryService.getProductCountInCategoryTree(id);
                return ResponseEntity.ok(count);
        }

        @GetMapping("/empty")
        @Operation(summary = "Get empty categories", description = "Retrieves all categories that don't have any products assigned")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Empty categories retrieved successfully")
        })
        public ResponseEntity<List<CategoryResponse>> getEmptyCategories() {
                log.debug("REST request to get empty categories");
                List<CategoryResponse> categories = categoryService.getEmptyCategories();
                return ResponseEntity.ok(categories);
        }

        @PutMapping("/{id}")
        @Operation(summary = "Update category", description = "Updates an existing category. When changing parent, validates against circular references and updates descendant levels")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Category updated successfully", content = @Content(schema = @Schema(implementation = CategoryResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Invalid input data or circular reference detected"),
                        @ApiResponse(responseCode = "404", description = "Category or parent category not found"),
                        @ApiResponse(responseCode = "409", description = "Duplicate category name")
        })
        public ResponseEntity<CategoryResponse> updateCategory(
                        @Parameter(description = "Category ID", required = true) @PathVariable Long id,

                        @Valid @RequestBody CategoryUpdateRequest request) {

                log.info("REST request to update category: {}", id);
                CategoryResponse response = categoryService.updateCategory(id, request);
                return ResponseEntity.ok(response);
        }

        @PatchMapping("/{id}")
        @Operation(summary = "Partially update category", description = "Updates specific fields of an existing category")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Category updated successfully", content = @Content(schema = @Schema(implementation = CategoryResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Category not found")
        })
        public ResponseEntity<CategoryResponse> patchCategory(
                        @Parameter(description = "Category ID", required = true) @PathVariable Long id,

                        @RequestBody CategoryUpdateRequest request) {

                log.info("REST request to partially update category: {}", id);
                CategoryResponse response = categoryService.updateCategory(id, request);
                return ResponseEntity.ok(response);
        }

        @DeleteMapping("/{id}")
        @Operation(summary = "Delete category", description = "Deletes a category. Fails if category has products or child categories")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "Category deleted successfully"),
                        @ApiResponse(responseCode = "404", description = "Category not found"),
                        @ApiResponse(responseCode = "409", description = "Category has products or child categories")
        })
        public ResponseEntity<Void> deleteCategory(
                        @Parameter(description = "Category ID", required = true) @PathVariable Long id) {

                log.info("REST request to delete category: {}", id);
                categoryService.deleteCategory(id);
                return ResponseEntity.noContent().build();
        }
}