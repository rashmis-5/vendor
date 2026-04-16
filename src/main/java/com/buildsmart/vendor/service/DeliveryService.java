package com.buildsmart.vendor.service;

import com.buildsmart.vendor.dto.DeliveryDTO;
import com.buildsmart.vendor.dto.PageableResponse;
import com.buildsmart.vendor.model.Delivery;

import java.util.List;

public interface DeliveryService {

    List<DeliveryDTO> getAllDeliveries();

    PageableResponse<DeliveryDTO> getAllDeliveries(int pageNumber, int pageSize, String sortBy, String sortDirection);

    DeliveryDTO getDeliveryById(String id);

    List<DeliveryDTO> getDeliveriesByContract(String contractId);

    PageableResponse<DeliveryDTO> getDeliveriesByContract(String contractId, int pageNumber, int pageSize, String sortBy, String sortDirection);

    List<DeliveryDTO> getDeliveriesByStatus(Delivery.DeliveryStatus status);

    PageableResponse<DeliveryDTO> getDeliveriesByStatus(Delivery.DeliveryStatus status, int pageNumber, int pageSize, String sortBy, String sortDirection);

    DeliveryDTO createDelivery(DeliveryDTO dto);

    DeliveryDTO updateDelivery(String id, DeliveryDTO dto);

    DeliveryDTO updateDeliveryStatus(String id, Delivery.DeliveryStatus status);

    void deleteDelivery(String id);
}

