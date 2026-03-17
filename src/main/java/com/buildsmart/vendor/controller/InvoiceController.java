package com.buildsmart.vendor.controller;
import com.buildsmart.vendor.dto.*;
import com.buildsmart.vendor.model.Invoice;
import com.buildsmart.vendor.service.InvoiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
public class InvoiceController {
    private final InvoiceService invoiceService;
    @GetMapping
    public ResponseEntity<ApiResponse<List<InvoiceDTO>>> getAll(
            @RequestParam(required = false) Long contractId,
            @RequestParam(required = false) Invoice.InvoiceStatus status) {
        List<InvoiceDTO> result;
        if (contractId != null) result = invoiceService.getInvoicesByContract(contractId);
        else if (status != null) result = invoiceService.getInvoicesByStatus(status);
        else result = invoiceService.getAllInvoices();
        return ResponseEntity.ok(ApiResponse.success(result, "Invoices retrieved"));
    }
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<InvoiceDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(invoiceService.getInvoiceById(id), "Invoice retrieved"));
    }
    @PostMapping
    public ResponseEntity<ApiResponse<InvoiceDTO>> create(@Valid @RequestBody InvoiceDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(invoiceService.createInvoice(dto), "Invoice created"));
    }
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<InvoiceDTO>> update(@PathVariable Long id, @Valid @RequestBody InvoiceDTO dto) {
        return ResponseEntity.ok(ApiResponse.success(invoiceService.updateInvoice(id, dto), "Invoice updated"));
    }
    @PostMapping("/{id}/upload")
    public ResponseEntity<ApiResponse<InvoiceDTO>> uploadFile(@PathVariable Long id,
            @RequestParam("file") MultipartFile file,
            @RequestParam(defaultValue = "vendor") String submittedBy) throws IOException {
        return ResponseEntity.ok(ApiResponse.success(
                invoiceService.uploadInvoiceFile(id, file, submittedBy), "Invoice file uploaded and submitted for approval"));
    }
    @PostMapping("/{id}/approval")
    public ResponseEntity<ApiResponse<InvoiceDTO>> processApproval(@PathVariable Long id,
            @RequestBody ApprovalRequest request) {
        return ResponseEntity.ok(ApiResponse.success(invoiceService.processApproval(id, request),
                request.isApproved() ? "Invoice approved" : "Invoice rejected"));
    }
    @PutMapping("/{id}/mark-paid")
    public ResponseEntity<ApiResponse<InvoiceDTO>> markAsPaid(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(invoiceService.markAsPaid(id), "Invoice marked as paid"));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        invoiceService.deleteInvoice(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Invoice deleted"));
    }
}
