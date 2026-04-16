package com.buildsmart.vendor.validator;

import com.buildsmart.vendor.dto.VendorDocumentDTO;
import com.buildsmart.vendor.model.VendorDocument;
import org.springframework.stereotype.Component;

@Component
public class VendorDocumentValidator {

    public void validate(VendorDocumentDTO dto) {

        // Document name must not be null or empty
        if (dto.getDocumentName() == null || dto.getDocumentName().trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Document name cannot be empty");
        }

        // File path must be present (upload is mandatory)
        if (dto.getFilePath() == null || dto.getFilePath().trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Document file path is required");
        }

        // Document type must be specified
        if (dto.getDocumentType() == null) {
            throw new IllegalArgumentException(
                    "Document type is required");
        }

        // Status must be specified
        if (dto.getStatus() == null) {
            throw new IllegalArgumentException(
                    "Document status is required");
        }

        // If document is rejected, rejection reason must be provided
        if (dto.getStatus() == VendorDocument.DocumentStatus.REJECTED &&
                (dto.getRejectionReason() == null ||
                        dto.getRejectionReason().trim().isEmpty())) {
            throw new IllegalArgumentException(
                    "Rejection reason must be provided for rejected documents");
        }
    }
}