package com.buildsmart.vendor.controller;

import com.buildsmart.vendor.dto.ApiResponse;
import com.buildsmart.vendor.dto.ProjectDTO;
import com.buildsmart.vendor.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
@Tag(name = "Project Management", description = "APIs for managing projects – create, retrieve, update, and delete projects")
public class ProjectController {
    private static final Logger log = LoggerFactory.getLogger(ProjectController.class);
    private final ProjectService projectService;

    @Operation(summary = "Get all projects", description = "Returns all projects, optionally filtered by name")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Projects retrieved successfully")
    })
    @GetMapping
    public ResponseEntity<ApiResponse<List<ProjectDTO>>> getAll(
            @Parameter(description = "Search projects by name (partial match)") @RequestParam(required = false) String name) {
        List<ProjectDTO> projects =
                (name == null || name.trim().isEmpty())
                        ? projectService.getAllProjects()
                        : projectService.searchByName(name);
        return ResponseEntity.ok(ApiResponse.success(projects, "Projects retrieved"));
    }

    @Operation(summary = "Get project by ID", description = "Retrieves a single project by its unique ID")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Project retrieved"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Project not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProjectDTO>> getById(
            @Parameter(description = "Project ID", required = true) @PathVariable String id) {
        log.info("Fetching project by id={}", id);
        return ResponseEntity.ok(ApiResponse.success(projectService.getProjectById(id), "Project retrieved"));
    }

    @Operation(summary = "Create a new project", description = "Creates a new project")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Project created"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    })
    @PostMapping
    public ResponseEntity<ApiResponse<ProjectDTO>> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Project details", required = true)
            @Valid @RequestBody ProjectDTO dto) {
        log.info("Creating new project, name={}", dto.getProjectName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(projectService.createProject(dto), "Project created"));
    }

    @Operation(summary = "Update a project", description = "Updates an existing project by ID")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Project updated"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Project not found", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProjectDTO>> update(
            @Parameter(description = "Project ID", required = true) @PathVariable String id,
            @Valid @RequestBody ProjectDTO dto) {
        log.info("Updating project id={}", id);
        return ResponseEntity.ok(ApiResponse.success(projectService.updateProject(id, dto), "Project updated"));
    }

    @Operation(summary = "Delete a project", description = "Deletes a project by ID")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Project deleted"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Project not found", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @Parameter(description = "Project ID", required = true) @PathVariable String id) {
        log.info("Deleting project id={}", id);
        projectService.deleteProject(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Project deleted"));
    }
}
