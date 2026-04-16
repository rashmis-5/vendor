package com.buildsmart.vendor.service.impl;

import com.buildsmart.vendor.dto.DeliveryDTO;
import com.buildsmart.vendor.dto.PageableResponse;
import com.buildsmart.vendor.exception.DuplicateResourceException;
import com.buildsmart.vendor.exception.ResourceNotFoundException;
import com.buildsmart.vendor.model.Contract;
import com.buildsmart.vendor.model.Delivery;
import com.buildsmart.vendor.repository.ContractRepository;
import com.buildsmart.vendor.repository.DeliveryRepository;
import com.buildsmart.vendor.service.DeliveryService;
import com.buildsmart.vendor.util.IdGeneratorUtil;
import com.buildsmart.vendor.validator.DeliveryValidator;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DeliveryServiceImpl implements DeliveryService {

    private static final Logger log = LoggerFactory.getLogger(DeliveryServiceImpl.class);

    private final DeliveryRepository deliveryRepository;
    private final ContractRepository contractRepository;
    private final DeliveryValidator deliveryValidator;

    @Override
    public List<DeliveryDTO> getAllDeliveries() {
        List<Delivery> deliveries = deliveryRepository.findAll();
        List<DeliveryDTO> result = new ArrayList<>();

        for (Delivery delivery : deliveries) {
            result.add(toDTO(delivery));
        }
        return result;
    }

    @Override
    public DeliveryDTO getDeliveryById(String id) {
        return toDTO(findById(id));
    }

    @Override
    public List<DeliveryDTO> getDeliveriesByContract(String contractId) {
        List<Delivery> deliveries =
                deliveryRepository.findByContractContractId(contractId);
        List<DeliveryDTO> result = new ArrayList<>();

        for (Delivery delivery : deliveries) {
            result.add(toDTO(delivery));
        }
        return result;
    }

    @Override
    public List<DeliveryDTO> getDeliveriesByStatus(Delivery.DeliveryStatus status) {
        List<Delivery> deliveries =
                deliveryRepository.findByStatus(status);
        List<DeliveryDTO> result = new ArrayList<>();

        for (Delivery delivery : deliveries) {
            result.add(toDTO(delivery));
        }
        return result;
    }

    @Override
    public DeliveryDTO createDelivery(DeliveryDTO dto) {
        log.info("Creating delivery for contractId={}, item={}", dto.getContractId(), dto.getItem());
        deliveryValidator.validate(dto);

        if (deliveryRepository.existsByContractContractIdAndDateAndItemIgnoreCase(
                dto.getContractId(), dto.getDate(), dto.getItem())) {
            throw new DuplicateResourceException(
                    "Delivery already exists for this contract, date, and item");
        }

        Contract contract = contractRepository.findById(dto.getContractId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Contract", dto.getContractId()));

        String lastId = deliveryRepository.findTopByOrderByDeliveryIdDesc()
                .map(Delivery::getDeliveryId)
                .orElse(null);
        String newId = IdGeneratorUtil.nextDeliveryId(lastId);

        Delivery delivery = Delivery.builder()
                .deliveryId(newId)
                .contract(contract)
                .date(dto.getDate())
                .item(dto.getItem())
                .quantity(dto.getQuantity())
                .unit(dto.getUnit())
                .notes(dto.getNotes())
                .deliveredBy(dto.getDeliveredBy())
                .receivedBy(dto.getReceivedBy())
                .status(dto.getStatus() != null
                        ? dto.getStatus()
                        : Delivery.DeliveryStatus.PENDING)
                .build();

        Delivery savedDelivery = deliveryRepository.save(delivery);
        return toDTO(savedDelivery);
    }

    @Override
    public DeliveryDTO updateDelivery(String id, DeliveryDTO dto) {
        log.info("Updating delivery id={}", id);
        deliveryValidator.validate(dto);

        if (deliveryRepository.existsByContractContractIdAndDateAndItemIgnoreCaseAndDeliveryIdNot(
                dto.getContractId(), dto.getDate(), dto.getItem(), id)) {
            throw new DuplicateResourceException(
                    "Delivery already exists for this contract, date, and item");
        }

        Delivery delivery = findById(id);

        delivery.setDate(dto.getDate());
        delivery.setItem(dto.getItem());
        delivery.setQuantity(dto.getQuantity());
        delivery.setUnit(dto.getUnit());
        delivery.setNotes(dto.getNotes());
        delivery.setDeliveredBy(dto.getDeliveredBy());
        delivery.setReceivedBy(dto.getReceivedBy());
        delivery.setStatus(dto.getStatus());

        Delivery updatedDelivery = deliveryRepository.save(delivery);
        return toDTO(updatedDelivery);
    }

    @Override
    public DeliveryDTO updateDeliveryStatus(String id, Delivery.DeliveryStatus status) {
        log.info("Updating status of delivery id={} to {}", id, status);
        Delivery delivery = findById(id);
        delivery.setStatus(status);

        Delivery updatedDelivery = deliveryRepository.save(delivery);
        return toDTO(updatedDelivery);
    }

    @Override
    public void deleteDelivery(String id) {
        log.info("Deleting delivery id={}", id);
        findById(id); // validate existence
        deliveryRepository.deleteById(id);
    }

    @Override
    public PageableResponse<DeliveryDTO> getAllDeliveries(int pageNumber, int pageSize, String sortBy, String sortDirection) {
        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(direction, sortBy));
        Page<Delivery> page = deliveryRepository.findAll(pageable);
        return convertToPageableResponse(page);
    }

    @Override
    public PageableResponse<DeliveryDTO> getDeliveriesByContract(String contractId, int pageNumber, int pageSize, String sortBy, String sortDirection) {
        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(direction, sortBy));
        Page<Delivery> page = deliveryRepository.findByContractContractId(contractId, pageable);
        return convertToPageableResponse(page);
    }

    @Override
    public PageableResponse<DeliveryDTO> getDeliveriesByStatus(Delivery.DeliveryStatus status, int pageNumber, int pageSize, String sortBy, String sortDirection) {
        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(direction, sortBy));
        Page<Delivery> page = deliveryRepository.findByStatus(status, pageable);
        return convertToPageableResponse(page);
    }

    // ---------- Private helper methods ----------

    private Delivery findById(String id) {
        return deliveryRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Delivery", id));
    }

    private DeliveryDTO toDTO(Delivery d) {
        return DeliveryDTO.builder()
                .deliveryId(d.getDeliveryId())
                .contractId(d.getContract().getContractId())
                .date(d.getDate())
                .item(d.getItem())
                .quantity(d.getQuantity())
                .unit(d.getUnit())
                .notes(d.getNotes())
                .deliveredBy(d.getDeliveredBy())
                .receivedBy(d.getReceivedBy())
                .status(d.getStatus())
                .build();
    }

    private PageableResponse<DeliveryDTO> convertToPageableResponse(Page<Delivery> page) {
        List<DeliveryDTO> content = new ArrayList<>();
        for (Delivery delivery : page.getContent()) {
            content.add(toDTO(delivery));
        }
        return PageableResponse.<DeliveryDTO>builder()
                .content(content)
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .isFirstPage(page.isFirst())
                .isLastPage(page.isLast())
                .build();
    }
}

