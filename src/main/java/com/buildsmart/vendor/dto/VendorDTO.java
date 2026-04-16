package com.buildsmart.vendor.dto;
import com.buildsmart.vendor.model.Vendor;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;
@Data @NoArgsConstructor @AllArgsConstructor @Builder
@Schema(description = "Data Transfer Object representing a Vendor")
public class VendorDTO {
    @Schema(description = "Unique identifier of the vendor", example = "VND-001", accessMode = Schema.AccessMode.READ_ONLY)
    private String vendorId;
    @NotBlank(message = "Vendor name is required")
    @Schema(description = "Full name of the vendor", example = "Acme Construction Ltd.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;
    @Schema(description = "Contact information of the vendor", example = "John Doe, +1-555-0100")
    private String contactInfo;
    @Email(message = "Valid email required")
    @Schema(description = "Email address of the vendor", example = "contact@acme.com")
    private String email;
    @Schema(description = "Phone number of the vendor", example = "+1-555-0100")
    private String phone;
    @Schema(description = "Physical address of the vendor", example = "123 Builder Lane, NY 10001")
    private String address;
    @Schema(description = "Current status of the vendor", example = "ACTIVE")
    private Vendor.VendorStatus status;
}
