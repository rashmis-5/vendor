package com.buildsmart.vendor.controller;
import com.buildsmart.vendor.dto.*;
import com.buildsmart.vendor.model.Invoice;
import com.buildsmart.vendor.service.InvoiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
@Tag(name = "Invoice Management", description = "APIs for managing invoices – create, retrieve, upload files, approve/reject, and mark as paid")
public class InvoiceController {
    private static final Logger log = LoggerFactory.getLogger(InvoiceController.class);
    private final InvoiceService invoiceService;

    @Operation(
        summary = "Get all invoices",
        description = "Returns a paginated or full list of invoices. Supports filtering by contractId or status."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Invoices retrieved successfully")
    })
    @GetMapping
    public ResponseEntity<?> getAll(
            @Parameter(description = "Filter by contract ID") @RequestParam(required = false) String contractId,
            @Parameter(description = "Filter by invoice status") @RequestParam(required = false) Invoice.InvoiceStatus status,
            @Parameter(description = "Page number (0-based)") @RequestParam(required = false, defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(required = false, defaultValue = "10") int size,
            @Parameter(description = "Field to sort by") @RequestParam(required = false, defaultValue = "invoiceId") String sortBy,
            @Parameter(description = "Sort direction: asc or desc") @RequestParam(required = false, defaultValue = "asc") String sortDirection) {

        if (page >= 0 && size > 0) {
            if (contractId != null) {
                PageableResponse<InvoiceDTO> result = invoiceService.getInvoicesByContract(contractId, page, size, sortBy, sortDirection);
                return ResponseEntity.ok(ApiResponse.success(result, "Invoices retrieved"));
            } else if (status != null) {
                PageableResponse<InvoiceDTO> result = invoiceService.getInvoicesByStatus(status, page, size, sortBy, sortDirection);
                return ResponseEntity.ok(ApiResponse.success(result, "Invoices retrieved"));
            } else {
                PageableResponse<InvoiceDTO> result = invoiceService.getAllInvoices(page, size, sortBy, sortDirection);
                return ResponseEntity.ok(ApiResponse.success(result, "Invoices retrieved"));
            }
        } else {
            List<InvoiceDTO> result;
            if (contractId != null) result = invoiceService.getInvoicesByContract(contractId);
            else if (status != null) result = invoiceService.getInvoicesByStatus(status);
            else result = invoiceService.getAllInvoices();
            return ResponseEntity.ok(ApiResponse.success(result, "Invoices retrieved"));
        }
    }

    @Operation(summary = "Get invoice by ID", description = "Retrieves a single invoice by its unique ID")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Invoice retrieved"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Invoice not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<InvoiceDTO>> getById(
            @Parameter(description = "Invoice ID", required = true) @PathVariable String id) {
        log.info("Fetching invoice by id={}", id);
        return ResponseEntity.ok(ApiResponse.success(invoiceService.getInvoiceById(id), "Invoice retrieved"));
    }

    @Operation(summary = "Create a new invoice", description = "Creates a new invoice for a contract")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Invoice created"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    })
    @PostMapping
    public ResponseEntity<ApiResponse<InvoiceDTO>> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Invoice details", required = true)
            @Valid @RequestBody InvoiceDTO dto) {
        log.info("Creating invoice for contractId={}", dto.getContractId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(invoiceService.createInvoice(dto), "Invoice created"));
    }

    @Operation(summary = "Update an invoice", description = "Updates an existing invoice by ID")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Invoice updated"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Invoice not found", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<InvoiceDTO>> update(
            @Parameter(description = "Invoice ID", required = true) @PathVariable String id,
            @Valid @RequestBody InvoiceDTO dto) {
        log.info("Updating invoice id={}", id);
        return ResponseEntity.ok(ApiResponse.success(invoiceService.updateInvoice(id, dto), "Invoice updated"));
    }

    @Operation(summary = "Upload invoice file", description = "Uploads a PDF file for an existing invoice and submits it for approval")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Invoice file uploaded and submitted for approval"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Invoice not found", content = @Content)
    })
    @PostMapping("/{id}/upload")
    public ResponseEntity<ApiResponse<InvoiceDTO>> uploadFile(
            @Parameter(description = "Invoice ID", required = true) @PathVariable String id,
            @Parameter(description = "Invoice PDF file", required = true) @RequestParam("file") MultipartFile file,
            @Parameter(description = "Name of the submitter") @RequestParam(defaultValue = "vendor") String submittedBy) throws IOException {
        log.info("Uploading file for invoice id={}, submittedBy={}", id, submittedBy);
        return ResponseEntity.ok(ApiResponse.success(
                invoiceService.uploadInvoiceFile(id, file, submittedBy), "Invoice file uploaded and submitted for approval"));
    }

    @Operation(summary = "Approve or reject an invoice", description = "Processes the approval or rejection of a submitted invoice")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Invoice approval processed"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Invoice not found", content = @Content)
    })
    @PostMapping("/{id}/approval")
    public ResponseEntity<ApiResponse<InvoiceDTO>> processApproval(
            @Parameter(description = "Invoice ID", required = true) @PathVariable String id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Approval or rejection details", required = true)
            @RequestBody ApprovalRequest request) {
        log.info("Processing approval for invoice id={}, approved={}, by={}", id, request.isApproved(), request.getApprovedBy());
        return ResponseEntity.ok(ApiResponse.success(invoiceService.processApproval(id, request),
                request.isApproved() ? "Invoice approved" : "Invoice rejected"));
    }

    @Operation(summary = "Mark invoice as paid", description = "Marks an approved invoice as paid")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Invoice marked as paid"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Invoice not found", content = @Content)
    })
    @PutMapping("/{id}/mark-paid")
    public ResponseEntity<ApiResponse<InvoiceDTO>> markAsPaid(
            @Parameter(description = "Invoice ID", required = true) @PathVariable String id) {
        log.info("Marking invoice id={} as paid", id);
        return ResponseEntity.ok(ApiResponse.success(invoiceService.markAsPaid(id), "Invoice marked as paid"));
    }

    @Operation(summary = "Update invoice status", description = "Directly updates the status of an invoice")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Invoice status updated"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Invoice not found", content = @Content)
    })
    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<InvoiceDTO>> updateStatus(
            @Parameter(description = "Invoice ID", required = true) @PathVariable String id,
            @Parameter(description = "New invoice status", required = true) @RequestParam Invoice.InvoiceStatus status) {
        log.info("Updating invoice id={} status to {}", id, status);
        return ResponseEntity.ok(ApiResponse.success(invoiceService.updateInvoiceStatus(id, status),
                "Invoice status updated to " + status));
    }

    @Operation(summary = "Delete an invoice", description = "Deletes an invoice by ID")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Invoice deleted"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Invoice not found", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @Parameter(description = "Invoice ID", required = true) @PathVariable String id) {
        log.info("Deleting invoice id={}", id);
        invoiceService.deleteInvoice(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Invoice deleted"));
    }
}
