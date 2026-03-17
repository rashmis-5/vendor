package com.buildsmart.vendor.dto;
import com.buildsmart.vendor.model.Vendor;
import jakarta.validation.constraints.*;
import lombok.*;
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class VendorDTO {
    private Long vendorId;
    @NotBlank(message = "Vendor name is required")
    private String name;
    private String contactInfo;
    @Email(message = "Valid email required")
    private String email;
    private String phone;
    private String address;
    private Vendor.VendorStatus status;
}
