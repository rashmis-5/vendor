package com.buildsmart.vendor.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(
        name = "contracts",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_contract_vendor_project_dates",
                        columnNames = {"vendor_id", "project_id", "start_date", "end_date"}
                )
        }
)
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Contract {

    @Id
    @Column(name="contract_id", nullable = false, updatable = false,length = 20)
    private String contractId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id", nullable = false)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private Vendor vendor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private Project project;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal value;

    private String description;
    private String terms;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContractStatus status;

    @OneToMany(mappedBy = "contract", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private List<Delivery> deliveries;

    @OneToMany(mappedBy = "contract", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private List<Invoice> invoices;

    @OneToMany(mappedBy = "contract", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private List<VendorDocument> documents;

    public enum ContractStatus {
        DRAFT, ACTIVE, COMPLETED, TERMINATED, EXPIRED, PENDING_APPROVAL
    }
}
