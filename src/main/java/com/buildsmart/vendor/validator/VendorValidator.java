package com.buildsmart.vendor.validator;

import com.buildsmart.vendor.dto.VendorDTO;
import org.springframework.stereotype.Component;

@Component
public class VendorValidator {

    public void validate(VendorDTO dto) {

        // Vendor name must not be null or empty
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Vendor name cannot be empty");
        }

        // Vendor status must not be null
        if (dto.getStatus() == null) {
            throw new IllegalArgumentException(
                    "Vendor status cannot be empty");
        }

        // Email validation - must end with @gmail.com
        if (dto.getEmail() != null && !dto.getEmail().trim().isEmpty()) {
            if (!dto.getEmail().toLowerCase().endsWith("@gmail.com")) {
                throw new IllegalArgumentException(
                        "Email must end with @gmail.com");
            }
        }

        // Phone validation - must be exactly 10 characters
        if (dto.getPhone() != null && !dto.getPhone().trim().isEmpty()) {
            String phoneOnly = dto.getPhone().replaceAll("[^0-9]", "");
            if (phoneOnly.length() != 10) {
                throw new IllegalArgumentException(
                        "Phone number must be exactly 10 digits");
            }
        }
    }
}
