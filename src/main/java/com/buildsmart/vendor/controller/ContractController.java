package com.buildsmart.vendor.controller;
import com.buildsmart.vendor.dto.ApiResponse;
import com.buildsmart.vendor.dto.ContractDTO;
import com.buildsmart.vendor.dto.PageableResponse;
import com.buildsmart.vendor.model.Contract;
import com.buildsmart.vendor.service.ContractService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/contracts")
@RequiredArgsConstructor
@Tag(name = "Contract Management", description = "APIs for managing vendor contracts – create, retrieve, update, and delete contracts")
public class ContractController {
    private static final Logger log = LoggerFactory.getLogger(ContractController.class);
    private final ContractService contractService;

    @Operation(
        summary = "Get all contracts",
        description = "Returns a paginated or full list of contracts. Supports filtering by vendorId, projectId, or status."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Contracts retrieved successfully")
    })
    @GetMapping
    public ResponseEntity<?> getAll(
            @Parameter(description = "Filter by vendor ID") @RequestParam(required = false) String vendorId,
            @Parameter(description = "Filter by project ID") @RequestParam(required = false) String projectId,
            @Parameter(description = "Filter by contract status") @RequestParam(required = false) Contract.ContractStatus status,
            @Parameter(description = "Page number (0-based)") @RequestParam(required = false, defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(required = false, defaultValue = "10") int size,
            @Parameter(description = "Field to sort by") @RequestParam(required = false, defaultValue = "contractId") String sortBy,
            @Parameter(description = "Sort direction: asc or desc") @RequestParam(required = false, defaultValue = "asc") String sortDirection) {

        if (page >= 0 && size > 0) {
            if (vendorId != null) {
                PageableResponse<ContractDTO> result = contractService.getContractsByVendor(vendorId, page, size, sortBy, sortDirection);
                return ResponseEntity.ok(ApiResponse.success(result, "Contracts retrieved"));
            } else if (projectId != null) {
                PageableResponse<ContractDTO> result = contractService.getContractsByProject(projectId, page, size, sortBy, sortDirection);
                return ResponseEntity.ok(ApiResponse.success(result, "Contracts retrieved"));
            } else if (status != null) {
                PageableResponse<ContractDTO> result = contractService.getContractsByStatus(status, page, size, sortBy, sortDirection);
                return ResponseEntity.ok(ApiResponse.success(result, "Contracts retrieved"));
            } else {
                PageableResponse<ContractDTO> result = contractService.getAllContracts(page, size, sortBy, sortDirection);
                return ResponseEntity.ok(ApiResponse.success(result, "Contracts retrieved"));
            }
        } else {
            List<ContractDTO> result;
            if (vendorId != null) result = contractService.getContractsByVendor(vendorId);
            else if (projectId != null) result = contractService.getContractsByProject(projectId);
            else if (status != null) result = contractService.getContractsByStatus(status);
            else result = contractService.getAllContracts();
            return ResponseEntity.ok(ApiResponse.success(result, "Contracts retrieved"));
        }
    }

    @Operation(summary = "Get contract by ID", description = "Retrieves a single contract by its unique ID")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Contract retrieved"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Contract not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ContractDTO>> getById(
            @Parameter(description = "Contract ID", required = true) @PathVariable String id) {
        log.info("Fetching contract by id={}", id);
        return ResponseEntity.ok(ApiResponse.success(contractService.getContractById(id), "Contract retrieved"));
    }

    @Operation(summary = "Create a new contract", description = "Creates a new vendor contract")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Contract created"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    })
    @PostMapping
    public ResponseEntity<ApiResponse<ContractDTO>> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Contract details", required = true)
            @Valid @RequestBody ContractDTO dto) {
        log.info("Creating new contract for vendorId={}, projectId={}", dto.getVendorId(), dto.getProjectId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(contractService.createContract(dto), "Contract created"));
    }

    @Operation(summary = "Update a contract", description = "Updates an existing contract by ID")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Contract updated"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Contract not found", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ContractDTO>> update(
            @Parameter(description = "Contract ID", required = true) @PathVariable String id,
            @Valid @RequestBody ContractDTO dto) {
        log.info("Updating contract id={}", id);
        return ResponseEntity.ok(ApiResponse.success(contractService.updateContract(id, dto), "Contract updated"));
    }

    @Operation(summary = "Update contract status", description = "Updates only the status of a contract")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Status updated"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Contract not found", content = @Content)
    })
    @PutMapping("/{id}/update-status")
    public ResponseEntity<ApiResponse<ContractDTO>> updateStatus(
            @Parameter(description = "Contract ID", required = true) @PathVariable String id,
            @Parameter(description = "New contract status", required = true) @RequestParam Contract.ContractStatus status) {
        log.info("Updating status of contract id={} to {}", id, status);
        return ResponseEntity.ok(ApiResponse.success(contractService.updateContractStatus(id, status), "Status updated"));
    }

    @Operation(summary = "Delete a contract", description = "Deletes a contract by ID")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Contract deleted"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Contract not found", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @Parameter(description = "Contract ID", required = true) @PathVariable String id) {
        log.info("Deleting contract id={}", id);
        contractService.deleteContract(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Contract deleted"));
    }
}
