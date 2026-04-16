package com.buildsmart.vendor.dto;
import com.buildsmart.vendor.model.Invoice;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
@Schema(description = "Data Transfer Object representing an Invoice")
public class InvoiceDTO {
    @Schema(description = "Unique identifier of the invoice", example = "INV-001", accessMode = Schema.AccessMode.READ_ONLY)
    private String invoiceId;

    @NotNull(message = "Contract ID is required")
    @Schema(description = "ID of the associated contract", example = "CON-001", requiredMode = Schema.RequiredMode.REQUIRED)
    private String contractId;

    @NotNull(message = "Amount is required")
    @Schema(description = "Total invoice amount", example = "25000.00", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal amount;

    @NotNull(message = "Invoice date is required")
    @Schema(description = "Date the invoice was issued", example = "2025-03-15", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate date;

    @Schema(description = "Payment due date", example = "2025-04-15")
    private LocalDate dueDate;

    @Schema(description = "Invoice reference number", example = "INV-2025-0042")
    private String invoiceNumber;

    @Schema(description = "Description of goods or services invoiced", example = "March material supply")
    private String description;

    @Schema(description = "File path of the uploaded invoice document", accessMode = Schema.AccessMode.READ_ONLY)
    private String invoiceFilePath;

    @Schema(description = "Original filename of the uploaded invoice", accessMode = Schema.AccessMode.READ_ONLY)
    private String invoiceFileName;

    @Schema(description = "MIME type of the uploaded invoice file", accessMode = Schema.AccessMode.READ_ONLY)
    private String invoiceFileType;

    @Schema(description = "Current status of the invoice", example = "PENDING")
    private Invoice.InvoiceStatus status;

    @Schema(description = "Name of the person who approved the invoice", accessMode = Schema.AccessMode.READ_ONLY)
    private String approvedBy;

    @Schema(description = "Timestamp when the invoice was approved", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime approvedAt;

    @Schema(description = "Name of the person who rejected the invoice", accessMode = Schema.AccessMode.READ_ONLY)
    private String rejectedBy;

    @Schema(description = "Timestamp when the invoice was rejected", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime rejectedAt;

    @Schema(description = "Reason provided for rejection", accessMode = Schema.AccessMode.READ_ONLY)
    private String rejectionReason;

    @Schema(description = "Name of the person who submitted the invoice", example = "vendor")
    private String submittedBy;

    @Schema(description = "Timestamp when the invoice was submitted", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime submittedAt;
}
