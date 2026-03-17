package com.buildsmart.vendor.controller;
import com.buildsmart.vendor.dto.ApiResponse;
import com.buildsmart.vendor.dto.ContractDTO;
import com.buildsmart.vendor.model.Contract;
import com.buildsmart.vendor.service.ContractService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/api/contracts")
@RequiredArgsConstructor
public class ContractController {
    private final ContractService contractService;
    @GetMapping
    public ResponseEntity<ApiResponse<List<ContractDTO>>> getAll(
            @RequestParam(required = false) Long vendorId,
            @RequestParam(required = false) String projectId,
            @RequestParam(required = false) Contract.ContractStatus status) {
        List<ContractDTO> result;
        if (vendorId != null) result = contractService.getContractsByVendor(vendorId);
        else if (projectId != null) result = contractService.getContractsByProject(projectId);
        else if (status != null) result = contractService.getContractsByStatus(status);
        else result = contractService.getAllContracts();
        return ResponseEntity.ok(ApiResponse.success(result, "Contracts retrieved"));
    }
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ContractDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(contractService.getContractById(id), "Contract retrieved"));
    }
    @PostMapping
    public ResponseEntity<ApiResponse<ContractDTO>> create(@Valid @RequestBody ContractDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(contractService.createContract(dto), "Contract created"));
    }
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ContractDTO>> update(@PathVariable Long id, @Valid @RequestBody ContractDTO dto) {
        return ResponseEntity.ok(ApiResponse.success(contractService.updateContract(id, dto), "Contract updated"));
    }
    @PutMapping("/{id}/update-status")
    public ResponseEntity<ApiResponse<ContractDTO>> updateStatus(@PathVariable Long id,
            @RequestParam Contract.ContractStatus status) {
        return ResponseEntity.ok(ApiResponse.success(contractService.updateContractStatus(id, status), "Status updated"));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        contractService.deleteContract(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Contract deleted"));
    }
}
