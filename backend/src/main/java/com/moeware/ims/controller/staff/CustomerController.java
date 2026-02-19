package com.moeware.ims.controller.staff;

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

import com.moeware.ims.dto.ApiResponseWpp;
import com.moeware.ims.dto.staff.customer.CustomerDTO;
import com.moeware.ims.service.staff.CustomerService;

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
 * REST Controller for Customer management
 * Provides endpoints for CRUD operations on customers
 */
@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Customers", description = "Customer management APIs")
public class CustomerController {

    private final CustomerService customerService;

    /**
     * Get all customers with pagination and filters
     */
    @Operation(summary = "Get all customers", description = "Retrieve a paginated list of customers with optional filters for type, active status, city, country, and search term")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved customers", content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "400", description = "Invalid parameters", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping
    public ResponseEntity<ApiResponseWpp<Page<CustomerDTO>>> getAllCustomers(
            @Parameter(description = "Filter by customer type (RETAIL, WHOLESALE, CORPORATE)") @RequestParam(required = false) String customerType,
            @Parameter(description = "Filter by active status") @RequestParam(required = false) Boolean isActive,
            @Parameter(description = "Filter by city") @RequestParam(required = false) String city,
            @Parameter(description = "Filter by country") @RequestParam(required = false) String country,
            @Parameter(description = "Search by name, code, email, or company") @RequestParam(required = false) String search,
            @PageableDefault(size = 20, sort = "contactName", direction = Sort.Direction.ASC) Pageable pageable) {

        log.info("GET /api/customers - Fetching customers with filters");

        Page<CustomerDTO> customers = customerService.getAllCustomers(
                customerType, isActive, city, country, search, pageable);

        return ResponseEntity.ok(ApiResponseWpp.success(customers));
    }

    /**
     * Get customer by ID
     */
    @Operation(summary = "Get customer by ID", description = "Retrieve detailed information about a specific customer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer found", content = @Content(schema = @Schema(implementation = CustomerDTO.class))),
            @ApiResponse(responseCode = "404", description = "Customer not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<CustomerDTO> getCustomerById(
            @Parameter(description = "Customer ID", required = true) @PathVariable Long id) {

        log.info("GET /api/customers/{} - Fetching customer", id);

        CustomerDTO customer = customerService.getCustomerById(id);
        return ResponseEntity.ok(customer);
    }

    /**
     * Get customer by code
     */
    @Operation(summary = "Get customer by code", description = "Retrieve customer information using the unique customer code")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer found", content = @Content(schema = @Schema(implementation = CustomerDTO.class))),
            @ApiResponse(responseCode = "404", description = "Customer not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/code/{code}")
    public ResponseEntity<CustomerDTO> getCustomerByCode(
            @Parameter(description = "Customer code", required = true) @PathVariable String code) {

        log.info("GET /api/customers/code/{} - Fetching customer by code", code);

        CustomerDTO customer = customerService.getCustomerByCode(code);
        return ResponseEntity.ok(customer);
    }

    /**
     * Create a new customer
     */
    @Operation(summary = "Create a new customer", description = "Add a new customer to the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Customer created successfully", content = @Content(schema = @Schema(implementation = CustomerDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input or duplicate code/email", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PostMapping
    public ResponseEntity<CustomerDTO> createCustomer(
            @Parameter(description = "Customer data", required = true) @Valid @RequestBody CustomerDTO customerDTO) {

        log.info("POST /api/customers - Creating new customer with code: {}", customerDTO.getCustomerCode());

        CustomerDTO createdCustomer = customerService.createCustomer(customerDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCustomer);
    }

    /**
     * Update customer (full update)
     */
    @Operation(summary = "Update customer", description = "Perform a full update of an existing customer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer updated successfully", content = @Content(schema = @Schema(implementation = CustomerDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
            @ApiResponse(responseCode = "404", description = "Customer not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<CustomerDTO> updateCustomer(
            @Parameter(description = "Customer ID", required = true) @PathVariable Long id,
            @Parameter(description = "Updated customer data", required = true) @Valid @RequestBody CustomerDTO customerDTO) {

        log.info("PUT /api/customers/{} - Updating customer", id);

        CustomerDTO updatedCustomer = customerService.updateCustomer(id, customerDTO);
        return ResponseEntity.ok(updatedCustomer);
    }

    /**
     * Partial update customer
     */
    @Operation(summary = "Partially update customer", description = "Update specific fields of an existing customer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer updated successfully", content = @Content(schema = @Schema(implementation = CustomerDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
            @ApiResponse(responseCode = "404", description = "Customer not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PatchMapping("/{id}")
    public ResponseEntity<CustomerDTO> patchCustomer(
            @Parameter(description = "Customer ID", required = true) @PathVariable Long id,
            @Parameter(description = "Partial customer data", required = true) @RequestBody CustomerDTO customerDTO) {

        log.info("PATCH /api/customers/{} - Partially updating customer", id);

        CustomerDTO updatedCustomer = customerService.patchCustomer(id, customerDTO);
        return ResponseEntity.ok(updatedCustomer);
    }

    /**
     * Soft delete customer (deactivate)
     */
    @Operation(summary = "Delete customer", description = "Soft delete a customer by setting isActive to false. Cannot delete customers with pending orders.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Customer deleted successfully", content = @Content),
            @ApiResponse(responseCode = "404", description = "Customer not found", content = @Content),
            @ApiResponse(responseCode = "409", description = "Cannot delete customer with pending orders", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(
            @Parameter(description = "Customer ID", required = true) @PathVariable Long id) {

        log.info("DELETE /api/customers/{} - Soft deleting customer", id);

        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get retail customers
     */
    @Operation(summary = "Get retail customers", description = "Retrieve all active retail customers")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved retail customers", content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/retail")
    public ResponseEntity<Page<CustomerDTO>> getRetailCustomers(
            @PageableDefault(size = 20, sort = "contactName", direction = Sort.Direction.ASC) Pageable pageable) {

        log.info("GET /api/customers/retail - Fetching retail customers");

        Page<CustomerDTO> customers = customerService.getRetailCustomers(pageable);
        return ResponseEntity.ok(customers);
    }

    /**
     * Get wholesale customers
     */
    @Operation(summary = "Get wholesale customers", description = "Retrieve all active wholesale customers")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved wholesale customers", content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/wholesale")
    public ResponseEntity<Page<CustomerDTO>> getWholesaleCustomers(
            @PageableDefault(size = 20, sort = "contactName", direction = Sort.Direction.ASC) Pageable pageable) {

        log.info("GET /api/customers/wholesale - Fetching wholesale customers");

        Page<CustomerDTO> customers = customerService.getWholesaleCustomers(pageable);
        return ResponseEntity.ok(customers);
    }

    /**
     * Get corporate customers
     */
    @Operation(summary = "Get corporate customers", description = "Retrieve all active corporate customers")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved corporate customers", content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/corporate")
    public ResponseEntity<Page<CustomerDTO>> getCorporateCustomers(
            @PageableDefault(size = 20, sort = "companyName", direction = Sort.Direction.ASC) Pageable pageable) {

        log.info("GET /api/customers/corporate - Fetching corporate customers");

        Page<CustomerDTO> customers = customerService.getCorporateCustomers(pageable);
        return ResponseEntity.ok(customers);
    }

    /**
     * Search customers
     */
    @Operation(summary = "Search customers", description = "Search customers by name, code, email, or company name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search completed successfully", content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/search")
    public ResponseEntity<Page<CustomerDTO>> searchCustomers(
            @Parameter(description = "Search term", required = true) @RequestParam String term,
            @PageableDefault(size = 20, sort = "contactName", direction = Sort.Direction.ASC) Pageable pageable) {

        log.info("GET /api/customers/search?term={} - Searching customers", term);

        Page<CustomerDTO> customers = customerService.searchCustomers(term, pageable);
        return ResponseEntity.ok(customers);
    }

    /**
     * Get active customer count
     */
    @Operation(summary = "Get active customer count", description = "Get the total number of active customers")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Count retrieved successfully", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/count/active")
    public ResponseEntity<Long> getActiveCustomerCount() {

        log.info("GET /api/customers/count/active - Getting active customer count");

        Long count = customerService.getActiveCustomerCount();
        return ResponseEntity.ok(count);
    }

    /**
     * Get customer count by type
     */
    @Operation(summary = "Get customer count by type", description = "Get the number of customers for a specific type (RETAIL, WHOLESALE, CORPORATE)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Count retrieved successfully", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid customer type", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/count/type/{type}")
    public ResponseEntity<Long> getCustomerCountByType(
            @Parameter(description = "Customer type (RETAIL, WHOLESALE, CORPORATE)", required = true) @PathVariable String type) {

        log.info("GET /api/customers/count/type/{} - Getting customer count by type", type);

        Long count = customerService.getCustomerCountByType(type.toUpperCase());
        return ResponseEntity.ok(count);
    }

    // Note: Additional endpoints for orders, invoices, payments, and statements
    // would be implemented here
    // These require SalesOrder, Invoice, and Payment entities and repositories to
    // be implemented first
    // GET /api/v1/customers/{id}/orders
    // GET /api/v1/customers/{id}/invoices
    // GET /api/v1/customers/{id}/payments
    // GET /api/v1/customers/{id}/statement
}