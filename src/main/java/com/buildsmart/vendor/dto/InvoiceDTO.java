package com.buildsmart.vendor.dto;
import com.buildsmart.vendor.model.Invoice;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class InvoiceDTO {
    private Long invoiceId;
    @NotNull(message = "Contract ID is required")
    private Long contractId;
    @NotNull(message = "Amount is required")
    private BigDecimal amount;
    @NotNull(message = "Invoice date is required")
    private LocalDate date;
    private LocalDate dueDate;
    private String invoiceNumber;
    private String description;
    private String invoiceFilePath;
    private String invoiceFileName;
    private String invoiceFileType;
    private Invoice.InvoiceStatus status;
    private String approvedBy;
    private LocalDateTime approvedAt;
    private String rejectionReason;
    private String submittedBy;
    private LocalDateTime submittedAt;
}
