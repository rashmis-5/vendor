package com.buildsmart.vendor.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "invoices")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long invoiceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id", nullable = false)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private Contract contract;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDate date;

    private LocalDate dueDate;
    private String invoiceNumber;
    private String description;

    private String invoiceFilePath;
    private String invoiceFileName;
    private String invoiceFileType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvoiceStatus status;

    private String approvedBy;
    private LocalDateTime approvedAt;
    private String rejectionReason;
    private String submittedBy;
    private LocalDateTime submittedAt;

    public enum InvoiceStatus {
        DRAFT, SUBMITTED, UNDER_REVIEW, APPROVED, REJECTED, PAID, CANCELLED
    }
}
