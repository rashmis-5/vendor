package com.buildsmart.vendor.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
@Schema(description = "Request payload for approving or rejecting an invoice or document")
public class ApprovalRequest {
    @Schema(description = "Name of the approver (required when approving)", example = "admin")
    private String approvedBy;

    @Schema(description = "Name of the person rejecting (required when rejecting)", example = "admin")
    private String rejectedBy;

    @Schema(description = "Reason for rejection (required when rejecting)", example = "Missing supporting documents")
    private String rejectionReason;

    @Schema(description = "true to approve, false to reject", example = "true", requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean approved;
}
