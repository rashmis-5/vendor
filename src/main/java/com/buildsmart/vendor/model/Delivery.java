package com.buildsmart.vendor.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(
        name = "deliveries",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_delivery_contract_date_item",
                        columnNames = {"contract_id", "date", "item"}
                )
        }
)
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Delivery {

    @Id
    @Column(name="delivery_id", nullable = false, updatable = false,length = 20)
    private String deliveryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id", nullable = false)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private Contract contract;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private String item;

    @Column(nullable = false)
    private Double quantity;

    private String unit;
    private String notes;
    private String deliveredBy;
    private String receivedBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryStatus status;

    public enum DeliveryStatus {
        PENDING, IN_TRANSIT, DELIVERED, PARTIALLY_DELIVERED, REJECTED, VERIFIED
    }
}
