package com.buildsmart.vendor.dto;
import com.buildsmart.vendor.model.VendorDocument;
import lombok.*;
import java.time.LocalDateTime;
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class VendorDocumentDTO {
    private Long documentId;
    private Long contractId;
    private String documentName;
    private String filePath;
    private String fileType;
    private Long fileSize;
    private VendorDocument.DocumentType documentType;
    private VendorDocument.DocumentStatus status;
    private String uploadedBy;
    private LocalDateTime uploadedAt;
    private String approvedBy;
    private LocalDateTime approvedAt;
    private String rejectionReason;
    private String description;
}
