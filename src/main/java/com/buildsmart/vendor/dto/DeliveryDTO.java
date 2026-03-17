package com.buildsmart.vendor.dto;
import com.buildsmart.vendor.model.Delivery;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class DeliveryDTO {
    private Long deliveryId;
    @NotNull(message = "Contract ID is required")
    private Long contractId;
    @NotNull(message = "Delivery date is required")
    private LocalDate date;
    @NotBlank(message = "Item name is required")
    private String item;
    @NotNull(message = "Quantity is required")
    private Double quantity;
    private String unit;
    private String notes;
    private String deliveredBy;
    private String receivedBy;
    private Delivery.DeliveryStatus status;
}
