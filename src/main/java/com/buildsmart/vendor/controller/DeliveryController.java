package com.buildsmart.vendor.controller;
import com.buildsmart.vendor.dto.ApiResponse;
import com.buildsmart.vendor.dto.DeliveryDTO;
import com.buildsmart.vendor.dto.PageableResponse;
import com.buildsmart.vendor.model.Delivery;
import com.buildsmart.vendor.service.DeliveryService;
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
@RequestMapping("/api/deliveries")
@RequiredArgsConstructor
@Tag(name = "Delivery Management", description = "APIs for logging and managing delivery records associated with contracts")
public class DeliveryController {
    private static final Logger log = LoggerFactory.getLogger(DeliveryController.class);
    private final DeliveryService deliveryService;

    @Operation(
        summary = "Get all deliveries",
        description = "Returns a paginated or full list of deliveries. Supports filtering by contractId or status."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Deliveries retrieved successfully")
    })
    @GetMapping
    public ResponseEntity<?> getAll(
            @Parameter(description = "Filter by contract ID") @RequestParam(required = false) String contractId,
            @Parameter(description = "Filter by delivery status") @RequestParam(required = false) Delivery.DeliveryStatus status,
            @Parameter(description = "Page number (0-based)") @RequestParam(required = false, defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(required = false, defaultValue = "10") int size,
            @Parameter(description = "Field to sort by") @RequestParam(required = false, defaultValue = "deliveryId") String sortBy,
            @Parameter(description = "Sort direction: asc or desc") @RequestParam(required = false, defaultValue = "asc") String sortDirection) {

        if (page >= 0 && size > 0) {
            if (contractId != null) {
                PageableResponse<DeliveryDTO> result = deliveryService.getDeliveriesByContract(contractId, page, size, sortBy, sortDirection);
                return ResponseEntity.ok(ApiResponse.success(result, "Deliveries retrieved"));
            } else if (status != null) {
                PageableResponse<DeliveryDTO> result = deliveryService.getDeliveriesByStatus(status, page, size, sortBy, sortDirection);
                return ResponseEntity.ok(ApiResponse.success(result, "Deliveries retrieved"));
            } else {
                PageableResponse<DeliveryDTO> result = deliveryService.getAllDeliveries(page, size, sortBy, sortDirection);
                return ResponseEntity.ok(ApiResponse.success(result, "Deliveries retrieved"));
            }
        } else {
            List<DeliveryDTO> result;
            if (contractId != null) result = deliveryService.getDeliveriesByContract(contractId);
            else if (status != null) result = deliveryService.getDeliveriesByStatus(status);
            else result = deliveryService.getAllDeliveries();
            return ResponseEntity.ok(ApiResponse.success(result, "Deliveries retrieved"));
        }
    }

    @Operation(summary = "Get delivery by ID", description = "Retrieves a single delivery record by its unique ID")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Delivery retrieved"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Delivery not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DeliveryDTO>> getById(
            @Parameter(description = "Delivery ID", required = true) @PathVariable String id) {
        log.info("Fetching delivery by id={}", id);
        return ResponseEntity.ok(ApiResponse.success(deliveryService.getDeliveryById(id), "Delivery retrieved"));
    }

    @Operation(summary = "Log a new delivery", description = "Creates a new delivery record for a contract")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Delivery logged successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    })
    @PostMapping
    public ResponseEntity<ApiResponse<DeliveryDTO>> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Delivery details", required = true)
            @Valid @RequestBody DeliveryDTO dto) {
        log.info("Creating new delivery for contractId={}", dto.getContractId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(deliveryService.createDelivery(dto), "Delivery logged"));
    }

    @Operation(summary = "Update a delivery", description = "Updates an existing delivery record by ID")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Delivery updated"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Delivery not found", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DeliveryDTO>> update(
            @Parameter(description = "Delivery ID", required = true) @PathVariable String id,
            @Valid @RequestBody DeliveryDTO dto) {
        log.info("Updating delivery id={}", id);
        return ResponseEntity.ok(ApiResponse.success(deliveryService.updateDelivery(id, dto), "Delivery updated"));
    }

    @Operation(summary = "Update delivery status", description = "Updates only the status of a delivery")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Status updated"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Delivery not found", content = @Content)
    })
    @PutMapping("/{id}/update-status")
    public ResponseEntity<ApiResponse<DeliveryDTO>> updateStatus(
            @Parameter(description = "Delivery ID", required = true) @PathVariable String id,
            @Parameter(description = "New delivery status", required = true) @RequestParam Delivery.DeliveryStatus status) {
        log.info("Updating status of delivery id={} to {}", id, status);
        return ResponseEntity.ok(ApiResponse.success(deliveryService.updateDeliveryStatus(id, status), "Status updated"));
    }

    @Operation(summary = "Delete a delivery", description = "Deletes a delivery record by ID")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Delivery deleted"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Delivery not found", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @Parameter(description = "Delivery ID", required = true) @PathVariable String id) {
        log.info("Deleting delivery id={}", id);
        deliveryService.deleteDelivery(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Delivery deleted"));
    }
}
