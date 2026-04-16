package com.buildsmart.vendor.validator;

import com.buildsmart.vendor.dto.ContractDTO;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class ContractValidator {

    public void validate(ContractDTO dto) {

        // Contract value must be > 0
        if (dto.getValue() == null ||
                dto.getValue().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "Contract value must be greater than zero");
        }

        // Start date and end date must be present
        if (dto.getStartDate() == null || dto.getEndDate() == null) {
            throw new IllegalArgumentException(
                    "Start date and end date are required");
        }


        // End date cannot be before or equal to start date
        if (dto.getEndDate().isBefore(dto.getStartDate()) || 
            dto.getEndDate().isEqual(dto.getStartDate())) {
            throw new IllegalArgumentException(
                    "Contract end date must be after start date");
        }
    }
}