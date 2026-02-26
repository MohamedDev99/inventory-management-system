package com.moeware.ims.controller.transaction;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.moeware.ims.dto.ApiResponseWpp;
import com.moeware.ims.dto.transaction.payment.PaymentRequest;
import com.moeware.ims.dto.transaction.payment.PaymentResponse;
import com.moeware.ims.dto.transaction.payment.RefundPaymentRequest;
import com.moeware.ims.dto.transaction.payment.UpdatePaymentStatusRequest;
import com.moeware.ims.enums.transaction.PaymentMethod;
import com.moeware.ims.enums.transaction.PaymentStatus;
import com.moeware.ims.service.transaction.PaymentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * REST controller for managing payment transactions.
 * Base path: /api/payments
 */
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Payments", description = "Payment transaction recording and management")
public class PaymentController {

        private final PaymentService paymentService;

        // ─── LIST / SEARCH ───────────────────────────────────────────────────────

        @GetMapping
        @PreAuthorize("hasAnyRole('ADMIN','MANAGER','VIEWER')")
        @Operation(summary = "List all payments", description = "Returns a paginated list of payments with optional filtering by customer, "
                        + "sales order, method, status, and date range.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Payments retrieved successfully")
        })
        public ResponseEntity<ApiResponseWpp<Page<PaymentResponse>>> getAllPayments(
                        @Parameter(description = "Search by payment number or reference number") @RequestParam(required = false) String search,
                        @Parameter(description = "Filter by customer ID") @RequestParam(required = false) Long customerId,
                        @Parameter(description = "Filter by sales order ID") @RequestParam(required = false) Long salesOrderId,
                        @Parameter(description = "Filter by payment method") @RequestParam(required = false) PaymentMethod paymentMethod,
                        @Parameter(description = "Filter by payment status") @RequestParam(required = false) PaymentStatus paymentStatus,
                        @Parameter(description = "Filter by payment date (from)", example = "2026-01-01") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                        @Parameter(description = "Filter by payment date (to)", example = "2026-12-31") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                        Pageable pageable) {
                return ResponseEntity.ok(ApiResponseWpp.success(
                                paymentService.getAllPayments(search, customerId, salesOrderId,
                                                paymentMethod, paymentStatus, startDate, endDate, pageable)));
        }

        @GetMapping("/methods")
        @PreAuthorize("hasAnyRole('ADMIN','MANAGER','WAREHOUSE_STAFF','VIEWER')")
        @Operation(summary = "Get available payment methods", description = "Returns the list of all accepted payment methods.")
        public ResponseEntity<ApiResponseWpp<List<String>>> getPaymentMethods() {
                return ResponseEntity.ok(ApiResponseWpp.success(paymentService.getPaymentMethods()));
        }

        // ─── SINGLE RESOURCE ─────────────────────────────────────────────────────

        @GetMapping("/{id}")
        @PreAuthorize("hasAnyRole('ADMIN','MANAGER','VIEWER')")
        @Operation(summary = "Get payment by ID", description = "Returns full payment details by internal ID.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Payment retrieved successfully"),
                        @ApiResponse(responseCode = "404", description = "Payment not found")
        })
        public ResponseEntity<ApiResponseWpp<PaymentResponse>> getPaymentById(@PathVariable Long id) {
                return ResponseEntity.ok(ApiResponseWpp.success(paymentService.getPaymentById(id)));
        }

        // ─── CREATE ──────────────────────────────────────────────────────────────

        @PostMapping
        @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
        @Operation(summary = "Record a payment", description = "Creates a new payment transaction record. The salesOrderId is optional "
                        + "(advance payments or account credits may not be linked to a specific order).")
        @ApiResponses({
                        @ApiResponse(responseCode = "201", description = "Payment recorded successfully"),
                        @ApiResponse(responseCode = "400", description = "Validation error"),
                        @ApiResponse(responseCode = "404", description = "Customer, sales order, or user not found")
        })
        public ResponseEntity<ApiResponseWpp<PaymentResponse>> recordPayment(
                        @Valid @RequestBody PaymentRequest request,
                        @Parameter(description = "ID of the user recording this payment transaction — typically an admin or finance manager") @RequestParam Long processedByUserId) {
                return ResponseEntity.status(HttpStatus.CREATED).body(
                                ApiResponseWpp.success(paymentService.recordPayment(request, processedByUserId)));
        }

        // ─── STATUS TRANSITIONS ──────────────────────────────────────────────────

        @PatchMapping("/{id}/status")
        @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
        @Operation(summary = "Update payment status", description = "Updates the status of a payment (e.g., PENDING → COMPLETED or FAILED).")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Payment status updated successfully"),
                        @ApiResponse(responseCode = "400", description = "Cannot change status (e.g., already refunded)"),
                        @ApiResponse(responseCode = "404", description = "Payment not found")
        })
        public ResponseEntity<ApiResponseWpp<PaymentResponse>> updatePaymentStatus(
                        @PathVariable Long id,
                        @Valid @RequestBody UpdatePaymentStatusRequest request) {
                return ResponseEntity.ok(ApiResponseWpp.success(
                                paymentService.updatePaymentStatus(id, request)));
        }

        @PostMapping("/{id}/refund")
        @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
        @Operation(summary = "Process payment refund", description = "Initiates a refund for a completed payment. Refund amount cannot exceed "
                        + "the original payment amount.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Refund processed successfully"),
                        @ApiResponse(responseCode = "400", description = "Payment not eligible for refund or amount exceeds original"),
                        @ApiResponse(responseCode = "404", description = "Payment not found")
        })
        public ResponseEntity<ApiResponseWpp<PaymentResponse>> processRefund(
                        @PathVariable Long id,
                        @Valid @RequestBody RefundPaymentRequest request,
                        @Parameter(description = "ID of the user authorizing and processing this refund") @RequestParam Long processedByUserId) {
                return ResponseEntity.ok(ApiResponseWpp.success(
                                paymentService.processRefund(id, request, processedByUserId)));
        }
}