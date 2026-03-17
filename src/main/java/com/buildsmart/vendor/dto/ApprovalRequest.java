package com.buildsmart.vendor.dto;
import lombok.*;
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ApprovalRequest {
    private String approvedBy;
    private String rejectionReason;
    private boolean approved;
}
