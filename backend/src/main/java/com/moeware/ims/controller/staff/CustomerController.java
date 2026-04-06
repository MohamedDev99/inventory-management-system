package com.moeware.ims.controller.staff;

import java.time.LocalDate;

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
import com.moeware.ims.dto.staff.customer.CustomerCreateRequest;
import com.moeware.ims.dto.staff.customer.CustomerResponse;
import com.moeware.ims.dto.staff.customer.CustomerStatementResponse;
import com.moeware.ims.dto.staff.customer.CustomerUpdateRequest;
import com.moeware.ims.dto.transaction.invoice.InvoiceResponse;
import com.moeware.ims.dto.transaction.payment.PaymentResponse;
import com.moeware.ims.dto.transaction.salesOrder.SalesOrderSummaryResponse;
import com.moeware.ims.enums.transaction.InvoiceStatus;
import com.moeware.ims.enums.transaction.PaymentMethod;
import com.moeware.ims.enums.transaction.PaymentStatus;
import com.moeware.ims.enums.transaction.SalesOrderStatus;
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
 * REST Controller for Customer management.
 * Provides endpoints for CRUD operations on customers.
 * All responses are wrapped in {@link ApiResponseWpp}.
 */
@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Customers", description = "Customer management APIs")
public class CustomerController {

        private final CustomerService customerService;

        // =========================================================================
        // CRUD
        // =========================================================================

        @Operation(summary = "Get all customers", description = "Retrieve a paginated list of customers with optional filters for type, active status, city, country, and search term")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Successfully retrieved customers", content = @Content(schema = @Schema(implementation = Page.class))),
                        @ApiResponse(responseCode = "400", description = "Invalid parameters", content = @Content),
                        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
        })
        @GetMapping
        public ResponseEntity<ApiResponseWpp<Page<CustomerResponse>>> getAllCustomers(
                        @Parameter(description = "Filter by customer type (RETAIL, WHOLESALE, CORPORATE)") @RequestParam(required = false) String customerType,
                        @Parameter(description = "Filter by active status") @RequestParam(required = false) Boolean isActive,
                        @Parameter(description = "Filter by city") @RequestParam(required = false) String city,
                        @Parameter(description = "Filter by country") @RequestParam(required = false) String country,
                        @Parameter(description = "Search by name, code, email, or company") @RequestParam(required = false) String search,
                        @PageableDefault(size = 20, sort = "contactName", direction = Sort.Direction.ASC) Pageable pageable) {

                log.info("GET /api/customers - Fetching customers with filters");

                Page<CustomerResponse> customers = customerService.getAllCustomers(
                                customerType, isActive, city, country, search, pageable);

                return ResponseEntity.ok(ApiResponseWpp.success(customers, "Customers retrieved successfully"));
        }

        @Operation(summary = "Get customer by ID", description = "Retrieve detailed information about a specific customer")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Customer found", content = @Content(schema = @Schema(implementation = CustomerResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Customer not found", content = @Content),
                        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
        })
        @GetMapping("/{id}")
        public ResponseEntity<ApiResponseWpp<CustomerResponse>> getCustomerById(
                        @Parameter(description = "Customer ID", required = true) @PathVariable Long id) {

                log.info("GET /api/customers/{} - Fetching customer", id);

                return ResponseEntity.ok(ApiResponseWpp.success(
                                customerService.getCustomerById(id), "Customer retrieved successfully"));
        }

        @Operation(summary = "Get customer by code", description = "Retrieve customer information using the unique customer code")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Customer found", content = @Content(schema = @Schema(implementation = CustomerResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Customer not found", content = @Content),
                        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
        })
        @GetMapping("/code/{code}")
        public ResponseEntity<ApiResponseWpp<CustomerResponse>> getCustomerByCode(
                        @Parameter(description = "Customer code", required = true) @PathVariable String code) {

                log.info("GET /api/customers/code/{} - Fetching customer by code", code);

                return ResponseEntity.ok(ApiResponseWpp.success(
                                customerService.getCustomerByCode(code), "Customer retrieved successfully"));
        }

        @Operation(summary = "Create a new customer", description = "Add a new customer to the system")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Customer created successfully", content = @Content(schema = @Schema(implementation = CustomerResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Invalid input or duplicate code/email", content = @Content),
                        @ApiResponse(responseCode = "409", description = "Conflict — customer code or email already exists", content = @Content),
                        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
        })
        @PostMapping
        public ResponseEntity<ApiResponseWpp<CustomerResponse>> createCustomer(
                        @Parameter(description = "Customer creation payload", required = true) @Valid @RequestBody CustomerCreateRequest request) {

                log.info("POST /api/customers - Creating new customer with code: {}", request.getCustomerCode());

                return ResponseEntity
                                .status(HttpStatus.CREATED)
                                .body(ApiResponseWpp.success(
                                                customerService.createCustomer(request),
                                                "Customer created successfully"));
        }

        @Operation(summary = "Update customer (full)", description = "Perform a full update of an existing customer. All updatable fields are applied.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Customer updated successfully", content = @Content(schema = @Schema(implementation = CustomerResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
                        @ApiResponse(responseCode = "404", description = "Customer not found", content = @Content),
                        @ApiResponse(responseCode = "409", description = "Conflict — duplicate email", content = @Content),
                        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
        })
        @PutMapping("/{id}")
        public ResponseEntity<ApiResponseWpp<CustomerResponse>> updateCustomer(
                        @Parameter(description = "Customer ID", required = true) @PathVariable Long id,
                        @Parameter(description = "Full update payload", required = true) @Valid @RequestBody CustomerUpdateRequest request) {

                log.info("PUT /api/customers/{} - Updating customer", id);

                return ResponseEntity.ok(ApiResponseWpp.success(
                                customerService.updateCustomer(id, request), "Customer updated successfully"));
        }

        @Operation(summary = "Patch customer (partial)", description = "Partially update a customer. Only non-null fields in the request body are applied.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Customer updated successfully", content = @Content(schema = @Schema(implementation = CustomerResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Invalid input or empty body", content = @Content),
                        @ApiResponse(responseCode = "404", description = "Customer not found", content = @Content),
                        @ApiResponse(responseCode = "409", description = "Conflict — duplicate email", content = @Content),
                        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
        })
        @PatchMapping("/{id}")
        public ResponseEntity<ApiResponseWpp<CustomerResponse>> patchCustomer(
                        @Parameter(description = "Customer ID", required = true) @PathVariable Long id,
                        @Parameter(description = "Partial update payload", required = true) @Valid @RequestBody CustomerUpdateRequest request) {

                log.info("PATCH /api/customers/{} - Partially updating customer", id);

                return ResponseEntity.ok(ApiResponseWpp.success(
                                customerService.patchCustomer(id, request), "Customer updated successfully"));
        }

        @Operation(summary = "Delete customer", description = "Soft-delete a customer by setting isActive to false. Blocked if the customer has pending orders.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Customer deactivated successfully", content = @Content),
                        @ApiResponse(responseCode = "404", description = "Customer not found", content = @Content),
                        @ApiResponse(responseCode = "409", description = "Cannot delete — customer has pending orders", content = @Content),
                        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
        })
        @DeleteMapping("/{id}")
        public ResponseEntity<ApiResponseWpp<Void>> deleteCustomer(
                        @Parameter(description = "Customer ID", required = true) @PathVariable Long id) {

                log.info("DELETE /api/customers/{} - Soft deleting customer", id);

                customerService.deleteCustomer(id);
                return ResponseEntity.ok(ApiResponseWpp.success(null, "Customer deactivated successfully"));
        }

        // =========================================================================
        // Segmented listing
        // =========================================================================

        @Operation(summary = "Get retail customers", description = "Retrieve all active retail customers")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Successfully retrieved retail customers", content = @Content(schema = @Schema(implementation = Page.class))),
                        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
        })
        @GetMapping("/retail")
        public ResponseEntity<ApiResponseWpp<Page<CustomerResponse>>> getRetailCustomers(
                        @PageableDefault(size = 20, sort = "contactName", direction = Sort.Direction.ASC) Pageable pageable) {

                log.info("GET /api/customers/retail - Fetching retail customers");
                return ResponseEntity.ok(ApiResponseWpp.success(
                                customerService.getRetailCustomers(pageable),
                                "Retail customers retrieved successfully"));
        }

        @Operation(summary = "Get wholesale customers", description = "Retrieve all active wholesale customers")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Successfully retrieved wholesale customers", content = @Content(schema = @Schema(implementation = Page.class))),
                        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
        })
        @GetMapping("/wholesale")
        public ResponseEntity<ApiResponseWpp<Page<CustomerResponse>>> getWholesaleCustomers(
                        @PageableDefault(size = 20, sort = "contactName", direction = Sort.Direction.ASC) Pageable pageable) {

                log.info("GET /api/customers/wholesale - Fetching wholesale customers");
                return ResponseEntity.ok(ApiResponseWpp.success(
                                customerService.getWholesaleCustomers(pageable),
                                "Wholesale customers retrieved successfully"));
        }

        @Operation(summary = "Get corporate customers", description = "Retrieve all active corporate customers")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Successfully retrieved corporate customers", content = @Content(schema = @Schema(implementation = Page.class))),
                        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
        })
        @GetMapping("/corporate")
        public ResponseEntity<ApiResponseWpp<Page<CustomerResponse>>> getCorporateCustomers(
                        @PageableDefault(size = 20, sort = "companyName", direction = Sort.Direction.ASC) Pageable pageable) {

                log.info("GET /api/customers/corporate - Fetching corporate customers");
                return ResponseEntity.ok(ApiResponseWpp.success(
                                customerService.getCorporateCustomers(pageable),
                                "Corporate customers retrieved successfully"));
        }

        // =========================================================================
        // Search & counts
        // =========================================================================

        @Operation(summary = "Search customers", description = "Search customers by name, code, email, or company name")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Search completed successfully", content = @Content(schema = @Schema(implementation = Page.class))),
                        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
        })
        @GetMapping("/search")
        public ResponseEntity<ApiResponseWpp<Page<CustomerResponse>>> searchCustomers(
                        @Parameter(description = "Search term", required = true) @RequestParam String term,
                        @PageableDefault(size = 20, sort = "contactName", direction = Sort.Direction.ASC) Pageable pageable) {

                log.info("GET /api/customers/search?term={} - Searching customers", term);
                return ResponseEntity.ok(ApiResponseWpp.success(
                                customerService.searchCustomers(term, pageable), "Search completed successfully"));
        }

        @Operation(summary = "Get active customer count", description = "Get the total number of active customers")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Count retrieved successfully", content = @Content),
                        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
        })
        @GetMapping("/count/active")
        public ResponseEntity<ApiResponseWpp<Long>> getActiveCustomerCount() {
                log.info("GET /api/customers/count/active - Getting active customer count");
                return ResponseEntity.ok(ApiResponseWpp.success(
                                customerService.getActiveCustomerCount(),
                                "Active customer count retrieved successfully"));
        }

        @Operation(summary = "Get customer count by type", description = "Get the number of customers for a specific type (RETAIL, WHOLESALE, CORPORATE)")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Count retrieved successfully", content = @Content),
                        @ApiResponse(responseCode = "400", description = "Invalid customer type", content = @Content),
                        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
        })
        @GetMapping("/count/type/{type}")
        public ResponseEntity<ApiResponseWpp<Long>> getCustomerCountByType(
                        @Parameter(description = "Customer type (RETAIL, WHOLESALE, CORPORATE)", required = true) @PathVariable String type) {

                log.info("GET /api/customers/count/type/{} - Getting customer count by type", type);
                return ResponseEntity.ok(ApiResponseWpp.success(
                                customerService.getCustomerCountByType(type.toUpperCase()),
                                "Customer count retrieved successfully"));
        }

        // =========================================================================
        // Sub-resource endpoints
        // =========================================================================

        @Operation(summary = "Get customer's sales orders", description = "Retrieve a paginated list of sales orders placed by this customer")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Successfully retrieved orders", content = @Content(schema = @Schema(implementation = Page.class))),
                        @ApiResponse(responseCode = "404", description = "Customer not found", content = @Content),
                        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
        })
        @GetMapping("/{id}/orders")
        public ResponseEntity<ApiResponseWpp<Page<SalesOrderSummaryResponse>>> getCustomerOrders(
                        @Parameter(description = "Customer ID", required = true) @PathVariable Long id,
                        @Parameter(description = "Filter by order status") @RequestParam(required = false) SalesOrderStatus status,
                        @Parameter(description = "Filter from date (inclusive)") @RequestParam(required = false) LocalDate startDate,
                        @Parameter(description = "Filter to date (inclusive)") @RequestParam(required = false) LocalDate endDate,
                        @PageableDefault(size = 20, sort = "orderDate", direction = Sort.Direction.DESC) Pageable pageable) {

                log.info("GET /api/customers/{}/orders - Fetching sales orders for customer", id);
                return ResponseEntity.ok(ApiResponseWpp.success(
                                customerService.getCustomerOrders(id, status, startDate, endDate, pageable),
                                "Customer orders retrieved successfully"));
        }

        @Operation(summary = "Get customer's invoices", description = "Retrieve a paginated list of invoices issued to this customer")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Successfully retrieved invoices", content = @Content(schema = @Schema(implementation = Page.class))),
                        @ApiResponse(responseCode = "404", description = "Customer not found", content = @Content),
                        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
        })
        @GetMapping("/{id}/invoices")
        public ResponseEntity<ApiResponseWpp<Page<InvoiceResponse>>> getCustomerInvoices(
                        @Parameter(description = "Customer ID", required = true) @PathVariable Long id,
                        @Parameter(description = "Filter by invoice status") @RequestParam(required = false) InvoiceStatus invoiceStatus,
                        @Parameter(description = "Filter from date (inclusive)") @RequestParam(required = false) LocalDate startDate,
                        @Parameter(description = "Filter to date (inclusive)") @RequestParam(required = false) LocalDate endDate,
                        @Parameter(description = "Show only overdue invoices") @RequestParam(required = false) Boolean overdue,
                        @PageableDefault(size = 20, sort = "invoiceDate", direction = Sort.Direction.DESC) Pageable pageable) {

                log.info("GET /api/customers/{}/invoices - Fetching invoices for customer", id);
                return ResponseEntity.ok(ApiResponseWpp.success(
                                customerService.getCustomerInvoices(id, invoiceStatus, startDate, endDate, overdue,
                                                pageable),
                                "Customer invoices retrieved successfully"));
        }

        @Operation(summary = "Get customer's payment history", description = "Retrieve a paginated list of payments made by this customer")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Successfully retrieved payments", content = @Content(schema = @Schema(implementation = Page.class))),
                        @ApiResponse(responseCode = "404", description = "Customer not found", content = @Content),
                        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
        })
        @GetMapping("/{id}/payments")
        public ResponseEntity<ApiResponseWpp<Page<PaymentResponse>>> getCustomerPayments(
                        @Parameter(description = "Customer ID", required = true) @PathVariable Long id,
                        @Parameter(description = "Filter by payment status") @RequestParam(required = false) PaymentStatus paymentStatus,
                        @Parameter(description = "Filter by payment method") @RequestParam(required = false) PaymentMethod paymentMethod,
                        @Parameter(description = "Filter from date (inclusive)") @RequestParam(required = false) LocalDate startDate,
                        @Parameter(description = "Filter to date (inclusive)") @RequestParam(required = false) LocalDate endDate,
                        @PageableDefault(size = 20, sort = "paymentDate", direction = Sort.Direction.DESC) Pageable pageable) {

                log.info("GET /api/customers/{}/payments - Fetching payment history for customer", id);
                return ResponseEntity.ok(ApiResponseWpp.success(
                                customerService.getCustomerPayments(id, paymentStatus, paymentMethod, startDate,
                                                endDate, pageable),
                                "Customer payment history retrieved successfully"));
        }

        @Operation(summary = "Get customer account statement", description = "Returns a chronological ledger of all invoices and payments within the date range, plus running balance, credit limit, and totals")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Statement retrieved successfully", content = @Content),
                        @ApiResponse(responseCode = "404", description = "Customer not found", content = @Content),
                        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
        })
        @GetMapping("/{id}/statement")
        public ResponseEntity<ApiResponseWpp<CustomerStatementResponse>> getCustomerStatement(
                        @Parameter(description = "Customer ID", required = true) @PathVariable Long id,
                        @Parameter(description = "Statement start date") @RequestParam(required = false) LocalDate startDate,
                        @Parameter(description = "Statement end date") @RequestParam(required = false) LocalDate endDate) {

                log.info("GET /api/customers/{}/statement - Fetching account statement for customer", id);
                return ResponseEntity.ok(ApiResponseWpp.success(
                                customerService.getCustomerStatement(id, startDate, endDate),
                                "Customer statement retrieved successfully"));
        }
}