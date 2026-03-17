package com.buildsmart.vendor.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "vendor_documents")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class VendorDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long documentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id", nullable = false)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private Contract contract;

    @Column(nullable = false)
    private String documentName;

    @Column(nullable = false)
    private String filePath;

    private String fileType;
    private Long fileSize;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DocumentType documentType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DocumentStatus status;

    private String uploadedBy;
    private LocalDateTime uploadedAt;
    private String approvedBy;
    private LocalDateTime approvedAt;
    private String rejectionReason;
    private String description;

    public enum DocumentType {
        INVOICE, PAYMENT_RECEIPT, CONTRACT_DOCUMENT, DELIVERY_CHALLAN,
        COMPLIANCE_CERTIFICATE, INSURANCE, OTHER
    }

    public enum DocumentStatus {
        UPLOADED, SUBMITTED, UNDER_REVIEW, APPROVED, REJECTED
    }
}
