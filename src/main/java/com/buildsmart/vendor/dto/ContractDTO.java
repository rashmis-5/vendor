package com.buildsmart.vendor.dto;
import com.buildsmart.vendor.model.Contract;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ContractDTO {
    private Long contractId;
    @NotNull(message = "Vendor ID is required")
    private Long vendorId;
    private String vendorName;
    @NotBlank(message = "Project ID is required")
    private String projectId;
    @NotNull(message = "Start date is required")
    private LocalDate startDate;
    @NotNull(message = "End date is required")
    private LocalDate endDate;
    @NotNull(message = "Contract value is required")
    private BigDecimal value;
    private String description;
    private String terms;
    private Contract.ContractStatus status;
}
