package com.buildsmart.vendor.validator;

import com.buildsmart.vendor.dto.InvoiceDTO;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class InvoiceValidator {

    public void validate(InvoiceDTO dto) {

        // Invoice amount must be greater than zero
        if (dto.getAmount() == null ||
                dto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "Invoice amount must be greater than zero");
        }

        // Invoice date must be present
        if (dto.getDate() == null) {
            throw new IllegalArgumentException(
                    "Invoice date is required");
        }

        // Invoice date cannot be in the future
        if (dto.getDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "Invoice date cannot be in the future");
        }

        // Due date must be after invoice date if present
        if (dto.getDueDate() != null && dto.getDueDate().isBefore(dto.getDate())) {
            throw new IllegalArgumentException(
                    "Invoice due date must be after invoice date");
        }
    }
}
