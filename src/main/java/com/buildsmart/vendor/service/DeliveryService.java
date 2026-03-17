package com.buildsmart.vendor.service;
import com.buildsmart.vendor.dto.DeliveryDTO;
import com.buildsmart.vendor.exception.ResourceNotFoundException;
import com.buildsmart.vendor.model.*;
import com.buildsmart.vendor.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class DeliveryService {
    private final DeliveryRepository deliveryRepository;
    private final ContractRepository contractRepository;
    public List<DeliveryDTO> getAllDeliveries() {
        return deliveryRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }
    public DeliveryDTO getDeliveryById(Long id) { return toDTO(findById(id)); }
    public List<DeliveryDTO> getDeliveriesByContract(Long contractId) {
        return deliveryRepository.findByContractContractId(contractId).stream().map(this::toDTO).collect(Collectors.toList());
    }
    public List<DeliveryDTO> getDeliveriesByStatus(Delivery.DeliveryStatus status) {
        return deliveryRepository.findByStatus(status).stream().map(this::toDTO).collect(Collectors.toList());
    }
    public DeliveryDTO createDelivery(DeliveryDTO dto) {
        Contract contract = contractRepository.findById(dto.getContractId())
                .orElseThrow(() -> new ResourceNotFoundException("Contract", dto.getContractId()));
        Delivery d = Delivery.builder().contract(contract).date(dto.getDate()).item(dto.getItem())
                .quantity(dto.getQuantity()).unit(dto.getUnit()).notes(dto.getNotes())
                .deliveredBy(dto.getDeliveredBy()).receivedBy(dto.getReceivedBy())
                .status(dto.getStatus() != null ? dto.getStatus() : Delivery.DeliveryStatus.PENDING).build();
        return toDTO(deliveryRepository.save(d));
    }
    public DeliveryDTO updateDelivery(Long id, DeliveryDTO dto) {
        Delivery d = findById(id);
        d.setDate(dto.getDate()); d.setItem(dto.getItem()); d.setQuantity(dto.getQuantity());
        d.setUnit(dto.getUnit()); d.setNotes(dto.getNotes()); d.setDeliveredBy(dto.getDeliveredBy());
        d.setReceivedBy(dto.getReceivedBy()); d.setStatus(dto.getStatus());
        return toDTO(deliveryRepository.save(d));
    }
    public DeliveryDTO updateDeliveryStatus(Long id, Delivery.DeliveryStatus status) {
        Delivery d = findById(id); d.setStatus(status); return toDTO(deliveryRepository.save(d));
    }
    public void deleteDelivery(Long id) { findById(id); deliveryRepository.deleteById(id); }
    private Delivery findById(Long id) {
        return deliveryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Delivery", id));
    }
    private DeliveryDTO toDTO(Delivery d) {
        return DeliveryDTO.builder().deliveryId(d.getDeliveryId()).contractId(d.getContract().getContractId())
                .date(d.getDate()).item(d.getItem()).quantity(d.getQuantity()).unit(d.getUnit())
                .notes(d.getNotes()).deliveredBy(d.getDeliveredBy()).receivedBy(d.getReceivedBy())
                .status(d.getStatus()).build();
    }
}
