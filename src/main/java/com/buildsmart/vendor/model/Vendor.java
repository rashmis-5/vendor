package com.buildsmart.vendor.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.util.List;

@Entity
@Table(
        name = "vendors",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_vendor_name", columnNames = "name"),
                @UniqueConstraint(name = "uk_vendor_email", columnNames = "email"),
                @UniqueConstraint(name = "uk_vendor_phone", columnNames = "phone")
        }
)
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Vendor {

    @Id
    @Column(name="vendor_id", nullable = false, updatable = false,length = 20)
    private String vendorId;

    @NotBlank(message = "Vendor name is required")
    @Column(nullable = false, unique = true)
    private String name;

    @Column(length = 500)
    private String contactInfo;

    @Column(unique = true)
    private String email;
    @Column(unique = true)
    private String phone;
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VendorStatus status;

    @OneToMany(mappedBy = "vendor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private List<Contract> contracts;

    public enum VendorStatus {
        ACTIVE, INACTIVE, BLACKLISTED, PENDING_APPROVAL
    }
}
