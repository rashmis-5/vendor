package com.buildsmart.vendor.controller;
import com.buildsmart.vendor.dto.ApiResponse;
import com.buildsmart.vendor.dto.DeliveryDTO;
import com.buildsmart.vendor.model.Delivery;
import com.buildsmart.vendor.service.DeliveryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/api/deliveries")
@RequiredArgsConstructor
public class DeliveryController {
    private final DeliveryService deliveryService;
    @GetMapping
    public ResponseEntity<ApiResponse<List<DeliveryDTO>>> getAll(
            @RequestParam(required = false) Long contractId,
            @RequestParam(required = false) Delivery.DeliveryStatus status) {
        List<DeliveryDTO> result;
        if (contractId != null) result = deliveryService.getDeliveriesByContract(contractId);
        else if (status != null) result = deliveryService.getDeliveriesByStatus(status);
        else result = deliveryService.getAllDeliveries();
        return ResponseEntity.ok(ApiResponse.success(result, "Deliveries retrieved"));
    }
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DeliveryDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(deliveryService.getDeliveryById(id), "Delivery retrieved"));
    }
    @PostMapping
    public ResponseEntity<ApiResponse<DeliveryDTO>> create(@Valid @RequestBody DeliveryDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(deliveryService.createDelivery(dto), "Delivery logged"));
    }
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DeliveryDTO>> update(@PathVariable Long id, @Valid @RequestBody DeliveryDTO dto) {
        return ResponseEntity.ok(ApiResponse.success(deliveryService.updateDelivery(id, dto), "Delivery updated"));
    }
    @PutMapping ("/{id}/update-status")
    public ResponseEntity<ApiResponse<DeliveryDTO>> updateStatus(@PathVariable Long id,
            @RequestParam Delivery.DeliveryStatus status) {
        return ResponseEntity.ok(ApiResponse.success(deliveryService.updateDeliveryStatus(id, status), "Status updated"));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        deliveryService.deleteDelivery(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Delivery deleted"));
    }
}
