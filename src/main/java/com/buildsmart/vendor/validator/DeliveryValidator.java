package com.buildsmart.vendor.validator;

import com.buildsmart.vendor.dto.DeliveryDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DeliveryValidator {

    public void validate(DeliveryDTO dto) {

        // Quantity must be greater than zero
        if (dto.getQuantity() == null || dto.getQuantity() <= 0) {
            throw new IllegalArgumentException(
                    "Delivery quantity must be greater than zero");
        }

        // Item name must not be null or empty
        if (dto.getItem() == null || dto.getItem().trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Delivery item cannot be empty");
        }

        // Delivery date must be present
        if (dto.getDate() == null) {
            throw new IllegalArgumentException(
                    "Delivery date is required");
        }

        // Delivery date cannot be in the future
        if (dto.getDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "Delivery date cannot be in the future");
        }
    }
}