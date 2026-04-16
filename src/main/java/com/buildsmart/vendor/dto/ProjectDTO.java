package com.buildsmart.vendor.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Data Transfer Object representing a Project")
public class ProjectDTO {

    @Schema(description = "Unique identifier of the project", example = "PRJ-001", accessMode = Schema.AccessMode.READ_ONLY)
    private String projectId;

    @NotBlank(message = "Project name is required")
    @Schema(description = "Name of the project", example = "Downtown Tower Phase 1", requiredMode = Schema.RequiredMode.REQUIRED)
    private String projectName;

    @Schema(description = "Detailed description of the project", example = "Construction of a 20-storey office tower in downtown area")
    private String description;
}
