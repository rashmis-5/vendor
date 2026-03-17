package com.buildsmart.vendor.controller;
import com.buildsmart.vendor.dto.ApiResponse;
import com.buildsmart.vendor.dto.VendorDTO;
import com.buildsmart.vendor.model.Vendor;
import com.buildsmart.vendor.service.VendorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/api/vendors")
@RequiredArgsConstructor
public class VendorController {
    private final VendorService vendorService;
    @GetMapping
    public ResponseEntity<ApiResponse<List<VendorDTO>>> getAll(
            @RequestParam(required = false) Vendor.VendorStatus status,
            @RequestParam(required = false) String name) {
        List<VendorDTO> vendors;
        if (status != null) vendors = vendorService.getVendorsByStatus(status);
        else if (name != null) vendors = vendorService.searchVendorsByName(name);
        else vendors = vendorService.getAllVendors();
        return ResponseEntity.ok(ApiResponse.success(vendors, "Vendors retrieved successfully"));
    }
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<VendorDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(vendorService.getVendorById(id), "Vendor retrieved"));
    }
    @PostMapping
    public ResponseEntity<ApiResponse<VendorDTO>> create(@Valid @RequestBody VendorDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(vendorService.createVendor(dto), "Vendor created successfully"));
    }
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<VendorDTO>> update(@PathVariable Long id, @Valid @RequestBody VendorDTO dto) {
        return ResponseEntity.ok(ApiResponse.success(vendorService.updateVendor(id, dto), "Vendor updated"));
    }
    @PutMapping("/{id}/update-status")
    public ResponseEntity<ApiResponse<VendorDTO>> updateStatus(@PathVariable Long id,
            @RequestParam Vendor.VendorStatus status) {
        return ResponseEntity.ok(ApiResponse.success(vendorService.updateVendorStatus(id, status), "Status updated"));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        vendorService.deleteVendor(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Vendor deleted"));
    }
}
