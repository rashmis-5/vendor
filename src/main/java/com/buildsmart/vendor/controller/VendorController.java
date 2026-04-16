package com.buildsmart.vendor.controller;
import com.buildsmart.vendor.dto.ApiResponse;
import com.buildsmart.vendor.dto.PageableResponse;
import com.buildsmart.vendor.dto.VendorDTO;
import com.buildsmart.vendor.model.Vendor;
import com.buildsmart.vendor.service.VendorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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
@RequestMapping("/api/vendors")
@RequiredArgsConstructor
@Tag(name = "Vendor Management", description = "APIs for managing vendors – create, retrieve, update, and delete vendor records")
public class VendorController {
    private static final Logger log = LoggerFactory.getLogger(VendorController.class);
    private final VendorService vendorService;

    @Operation(
        summary = "Get all vendors",
        description = "Returns a paginated or full list of vendors. Supports filtering by status or searching by name."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Vendors retrieved successfully")
    })
    @GetMapping
    public ResponseEntity<?> getAll(
            @Parameter(description = "Filter vendors by status") @RequestParam(required = false) Vendor.VendorStatus status,
            @Parameter(description = "Search vendors by name (partial match)") @RequestParam(required = false) String name,
            @Parameter(description = "Page number (0-based)") @RequestParam(required = false, defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(required = false, defaultValue = "10") int size,
            @Parameter(description = "Field to sort by") @RequestParam(required = false, defaultValue = "vendorId") String sortBy,
            @Parameter(description = "Sort direction: asc or desc") @RequestParam(required = false, defaultValue = "asc") String sortDirection) {

        if (page >= 0 && size > 0) {
            if (status != null) {
                PageableResponse<VendorDTO> result = vendorService.getVendorsByStatus(status, page, size, sortBy, sortDirection);
                return ResponseEntity.ok(ApiResponse.success(result, "Vendors retrieved successfully"));
            } else if (name != null) {
                PageableResponse<VendorDTO> result = vendorService.searchVendorsByName(name, page, size, sortBy, sortDirection);
                return ResponseEntity.ok(ApiResponse.success(result, "Vendors retrieved successfully"));
            } else {
                PageableResponse<VendorDTO> result = vendorService.getAllVendors(page, size, sortBy, sortDirection);
                return ResponseEntity.ok(ApiResponse.success(result, "Vendors retrieved successfully"));
            }
        } else {
            List<VendorDTO> vendors;
            if (status != null) vendors = vendorService.getVendorsByStatus(status);
            else if (name != null) vendors = vendorService.searchVendorsByName(name);
            else vendors = vendorService.getAllVendors();
            return ResponseEntity.ok(ApiResponse.success(vendors, "Vendors retrieved successfully"));
        }
    }

    @Operation(summary = "Get vendor by ID", description = "Retrieves a single vendor by their unique ID")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Vendor retrieved"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Vendor not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<VendorDTO>> getById(
            @Parameter(description = "Vendor ID", required = true) @PathVariable String id) {
        log.info("Fetching vendor by id={}", id);
        return ResponseEntity.ok(ApiResponse.success(vendorService.getVendorById(id), "Vendor retrieved"));
    }

    @Operation(summary = "Create a new vendor", description = "Creates a new vendor record")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Vendor created successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    })
    @PostMapping
    public ResponseEntity<ApiResponse<VendorDTO>> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Vendor details", required = true)
            @Valid @RequestBody VendorDTO dto) {
        log.info("Creating new vendor, name={}", dto.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(vendorService.createVendor(dto), "Vendor created successfully"));
    }

    @Operation(summary = "Update a vendor", description = "Updates an existing vendor by ID")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Vendor updated"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Vendor not found", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<VendorDTO>> update(
            @Parameter(description = "Vendor ID", required = true) @PathVariable String id,
            @Valid @RequestBody VendorDTO dto) {
        log.info("Updating vendor id={}", id);
        return ResponseEntity.ok(ApiResponse.success(vendorService.updateVendor(id, dto), "Vendor updated"));
    }

    @Operation(summary = "Update vendor status", description = "Updates only the status of a vendor")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Status updated"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Vendor not found", content = @Content)
    })
    @PutMapping("/{id}/update-status")
    public ResponseEntity<ApiResponse<VendorDTO>> updateStatus(
            @Parameter(description = "Vendor ID", required = true) @PathVariable String id,
            @Parameter(description = "New vendor status", required = true) @RequestParam Vendor.VendorStatus status) {
        log.info("Updating status of vendor id={} to {}", id, status);
        return ResponseEntity.ok(ApiResponse.success(vendorService.updateVendorStatus(id, status), "Status updated"));
    }

    @Operation(summary = "Delete a vendor", description = "Deletes a vendor by ID")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Vendor deleted"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Vendor not found", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @Parameter(description = "Vendor ID", required = true) @PathVariable String id) {
        log.info("Deleting vendor id={}", id);
        vendorService.deleteVendor(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Vendor deleted"));
    }
}
