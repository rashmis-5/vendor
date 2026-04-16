package com.buildsmart.vendor.dto;
import com.buildsmart.vendor.model.Contract;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
@Data @NoArgsConstructor @AllArgsConstructor @Builder
@Schema(description = "Data Transfer Object representing a Contract")
public class ContractDTO {
    @Schema(description = "Unique identifier of the contract", example = "CON-001", accessMode = Schema.AccessMode.READ_ONLY)
    private String contractId;
    @NotNull(message = "Vendor ID is required")
    @Schema(description = "ID of the associated vendor", example = "VND-001", requiredMode = Schema.RequiredMode.REQUIRED)
    private String vendorId;
    @Schema(description = "Name of the associated vendor", example = "Acme Construction Ltd.", accessMode = Schema.AccessMode.READ_ONLY)
    private String vendorName;
    @NotBlank(message = "Project ID is required")
    @Schema(description = "ID of the associated project", example = "PRJ-001", requiredMode = Schema.RequiredMode.REQUIRED)
    private String projectId;
    @Schema(description = "Name of the associated project", example = "Downtown Tower Phase 1", accessMode = Schema.AccessMode.READ_ONLY)
    private String projectName;
    @NotNull(message = "Start date is required")
    @Schema(description = "Contract start date", example = "2025-01-01", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate startDate;
    @NotNull(message = "End date is required")
    @Schema(description = "Contract end date", example = "2025-12-31", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate endDate;
    @NotNull(message = "Contract value is required")
    @Schema(description = "Total monetary value of the contract", example = "150000.00", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal value;
    @Schema(description = "Description of the contract scope", example = "Supply of construction materials")
    private String description;
    @Schema(description = "Terms and conditions of the contract", example = "Payment within 30 days of delivery")
    private String terms;
    @Schema(description = "Current status of the contract", example = "ACTIVE")
    private Contract.ContractStatus status;
}
