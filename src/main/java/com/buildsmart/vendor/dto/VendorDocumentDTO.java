package com.buildsmart.vendor.dto;
import com.buildsmart.vendor.model.VendorDocument;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.time.LocalDateTime;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
@Schema(description = "Data Transfer Object representing a Vendor Document")
public class VendorDocumentDTO {
    @Schema(description = "Unique identifier of the document", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long documentId;

    @Schema(description = "ID of the associated contract", example = "CON-001")
    private String contractId;

    @Schema(description = "Name of the document", example = "compliance_certificate.pdf", accessMode = Schema.AccessMode.READ_ONLY)
    private String documentName;

    @Schema(description = "Stored file path", accessMode = Schema.AccessMode.READ_ONLY)
    private String filePath;

    @Schema(description = "MIME type of the file", example = "application/pdf", accessMode = Schema.AccessMode.READ_ONLY)
    private String fileType;

    @Schema(description = "Size of the file in bytes", example = "204800", accessMode = Schema.AccessMode.READ_ONLY)
    private Long fileSize;

    @Schema(description = "Type/category of the document", example = "COMPLIANCE")
    private VendorDocument.DocumentType documentType;

    @Schema(description = "Current status of the document", example = "PENDING")
    private VendorDocument.DocumentStatus status;

    @Schema(description = "Name of the person who uploaded the document", example = "vendor")
    private String uploadedBy;

    @Schema(description = "Timestamp when the document was uploaded", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime uploadedAt;

    @Schema(description = "Name of the person who approved the document", accessMode = Schema.AccessMode.READ_ONLY)
    private String approvedBy;

    @Schema(description = "Timestamp when the document was approved", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime approvedAt;

    @Schema(description = "Name of the person who rejected the document", accessMode = Schema.AccessMode.READ_ONLY)
    private String rejectedBy;

    @Schema(description = "Timestamp when the document was rejected", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime rejectedAt;

    @Schema(description = "Reason for rejection", accessMode = Schema.AccessMode.READ_ONLY)
    private String rejectionReason;

    @Schema(description = "Optional description of the document", example = "Annual compliance certificate 2025")
    private String description;
}
