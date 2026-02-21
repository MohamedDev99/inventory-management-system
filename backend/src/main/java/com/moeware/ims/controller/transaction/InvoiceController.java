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
import com.moeware.ims.dto.transaction.invoice.InvoiceRequest;
import com.moeware.ims.dto.transaction.invoice.InvoiceResponse;
import com.moeware.ims.dto.transaction.invoice.RecordInvoicePaymentRequest;
import com.moeware.ims.dto.transaction.invoice.UpdateInvoiceStatusRequest;
import com.moeware.ims.enums.transaction.InvoiceStatus;
import com.moeware.ims.service.transaction.InvoiceService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * REST controller for managing invoices.
 * Base path: /api/v1/invoices
 */
@RestController
@RequestMapping("/api/v1/invoices")
@RequiredArgsConstructor
@Tag(name = "Invoices", description = "Invoice generation and billing management for sales orders")
public class InvoiceController {

    private final InvoiceService invoiceService;

    // ─── LIST / SEARCH ───────────────────────────────────────────────────────

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','VIEWER')")
    @Operation(summary = "List all invoices", description = "Returns a paginated list of invoices with optional filtering by customer, "
            + "sales order, status, date range, and overdue flag.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Invoices retrieved successfully")
    })
    public ResponseEntity<ApiResponseWpp<Page<InvoiceResponse>>> getAllInvoices(
            @Parameter(description = "Search by invoice number (partial match)") @RequestParam(required = false) String search,
            @Parameter(description = "Filter by customer ID") @RequestParam(required = false) Long customerId,
            @Parameter(description = "Filter by sales order ID") @RequestParam(required = false) Long salesOrderId,
            @Parameter(description = "Filter by invoice status") @RequestParam(required = false) InvoiceStatus invoiceStatus,
            @Parameter(description = "Filter by invoice date (from)", example = "2026-01-01") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "Filter by invoice date (to)", example = "2026-12-31") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @Parameter(description = "Filter invoices due by this date") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueDate,
            @Parameter(description = "If true, returns only overdue invoices") @RequestParam(required = false) Boolean overdue,
            Pageable pageable) {
        return ResponseEntity.ok(ApiResponseWpp.success(
                invoiceService.getAllInvoices(search, customerId, salesOrderId, invoiceStatus,
                        startDate, endDate, dueDate, overdue, pageable)));
    }

    @GetMapping("/overdue")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','VIEWER')")
    @Operation(summary = "Get overdue invoices", description = "Returns all invoices that are past their due date and have an outstanding balance.")
    public ResponseEntity<ApiResponseWpp<List<InvoiceResponse>>> getOverdueInvoices() {
        return ResponseEntity.ok(ApiResponseWpp.success(invoiceService.getOverdueInvoices()));
    }

    // ─── SINGLE RESOURCE ─────────────────────────────────────────────────────

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','VIEWER')")
    @Operation(summary = "Get invoice by ID", description = "Returns full invoice details by internal ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Invoice retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Invoice not found")
    })
    public ResponseEntity<ApiResponseWpp<InvoiceResponse>> getInvoiceById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponseWpp.success(invoiceService.getInvoiceById(id)));
    }

    // ─── CREATE ──────────────────────────────────────────────────────────────

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @Operation(summary = "Generate invoice", description = "Generates a new DRAFT invoice from a sales order. Financial amounts "
            + "(subtotal, tax, total) are derived directly from the linked sales order. "
            + "Only one invoice per sales order is allowed.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Invoice generated successfully"),
            @ApiResponse(responseCode = "400", description = "Invoice already exists for this sales order"),
            @ApiResponse(responseCode = "404", description = "Sales order or user not found")
    })
    public ResponseEntity<ApiResponseWpp<InvoiceResponse>> generateInvoice(
            @Valid @RequestBody InvoiceRequest request,
            @Parameter(description = "ID of the user generating this invoice") @RequestParam Long generatedByUserId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponseWpp.success(invoiceService.generateInvoice(request, generatedByUserId)));
    }

    // ─── STATUS TRANSITIONS ──────────────────────────────────────────────────

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @Operation(summary = "Update invoice status", description = "Updates invoice status and optionally records a paid amount. "
            + "When paidAmount is provided, balance due is automatically recalculated and "
            + "status is set to PAID or PARTIAL accordingly.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Invoice status updated successfully"),
            @ApiResponse(responseCode = "400", description = "Cannot update a cancelled invoice"),
            @ApiResponse(responseCode = "404", description = "Invoice not found")
    })
    public ResponseEntity<ApiResponseWpp<InvoiceResponse>> updateInvoiceStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateInvoiceStatusRequest request) {
        return ResponseEntity.ok(ApiResponseWpp.success(
                invoiceService.updateInvoiceStatus(id, request)));
    }

    @PostMapping("/{id}/send")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @Operation(summary = "Send invoice to customer", description = "Marks the invoice as SENT and (in a full implementation) triggers "
            + "email delivery of the invoice PDF to the customer.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Invoice sent successfully"),
            @ApiResponse(responseCode = "400", description = "Invoice is cancelled or already paid"),
            @ApiResponse(responseCode = "404", description = "Invoice not found")
    })
    public ResponseEntity<ApiResponseWpp<InvoiceResponse>> sendInvoice(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponseWpp.success(invoiceService.sendInvoice(id)));
    }

    @PostMapping("/{id}/payment")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @Operation(summary = "Record payment for invoice", description = "Records a payment against this invoice, updates the paid amount and balance due, "
            + "and automatically creates a corresponding Payment record. "
            + "Status is set to PARTIAL or PAID based on remaining balance.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Payment recorded successfully"),
            @ApiResponse(responseCode = "400", description = "Invoice cancelled/paid, or payment amount exceeds balance due"),
            @ApiResponse(responseCode = "404", description = "Invoice or user not found")
    })
    public ResponseEntity<ApiResponseWpp<InvoiceResponse>> recordPayment(
            @PathVariable Long id,
            @Valid @RequestBody RecordInvoicePaymentRequest request,
            @Parameter(description = "ID of the user recording this payment") @RequestParam Long processedByUserId) {
        return ResponseEntity.ok(ApiResponseWpp.success(
                invoiceService.recordPaymentForInvoice(id, request, processedByUserId)));
    }

    /**
     * PDF download endpoint placeholder.
     * In a full implementation this would return a binary PDF stream
     * (application/pdf).
     * Wire up your PDF generation library (iText, JasperReports, etc.) here.
     */
    @GetMapping("/{id}/pdf")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','VIEWER')")
    @Operation(summary = "Download invoice PDF", description = "Returns the invoice as a PDF file for download or printing. "
            + "NOTE: PDF generation is a TODO — integrate your PDF library here.")
    public ResponseEntity<ApiResponseWpp<String>> downloadInvoicePdf(@PathVariable Long id) {
        // Verify invoice exists (throws 404 if not)
        invoiceService.getInvoiceById(id);
        // TODO: Integrate PDF generation (iText / JasperReports / Thymeleaf + Flying
        // Saucer)
        return ResponseEntity.ok(ApiResponseWpp.success(
                "PDF generation for invoice ID " + id + " is not yet implemented."));
    }
}