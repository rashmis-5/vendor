package com.buildsmart.vendor.dto;
import com.buildsmart.vendor.model.Delivery;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;
@Data @NoArgsConstructor @AllArgsConstructor @Builder
@Schema(description = "Data Transfer Object representing a Delivery record")
public class DeliveryDTO {
    @Schema(description = "Unique identifier of the delivery", example = "DEL-001", accessMode = Schema.AccessMode.READ_ONLY)
    private String deliveryId;
    @NotNull(message = "Contract ID is required")
    @Schema(description = "ID of the associated contract", example = "CON-001", requiredMode = Schema.RequiredMode.REQUIRED)
    private String contractId;
    @NotNull(message = "Delivery date is required")
    @Schema(description = "Date of delivery", example = "2025-03-20", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate date;
    @NotBlank(message = "Item name is required")
    @Schema(description = "Name of the delivered item", example = "Steel Beams", requiredMode = Schema.RequiredMode.REQUIRED)
    private String item;
    @NotNull(message = "Quantity is required")
    @Schema(description = "Quantity of items delivered", example = "50.0", requiredMode = Schema.RequiredMode.REQUIRED)
    private Double quantity;
    @Schema(description = "Unit of measurement", example = "tons")
    private String unit;
    @Schema(description = "Additional notes about the delivery", example = "Delivered to site gate B")
    private String notes;
    @Schema(description = "Name of the person who delivered", example = "Mike Smith")
    private String deliveredBy;
    @Schema(description = "Name of the person who received the delivery", example = "Jane Doe")
    private String receivedBy;
    @Schema(description = "Current status of the delivery", example = "DELIVERED")
    private Delivery.DeliveryStatus status;
}
