package com.buildsmart.vendor.service.impl;

import com.buildsmart.vendor.dto.PageableResponse;
import com.buildsmart.vendor.dto.VendorDTO;
import com.buildsmart.vendor.exception.DuplicateResourceException;
import com.buildsmart.vendor.exception.ResourceNotFoundException;
import com.buildsmart.vendor.model.Vendor;
import com.buildsmart.vendor.repository.VendorRepository;
import com.buildsmart.vendor.service.VendorService;
import com.buildsmart.vendor.util.IdGeneratorUtil;
import com.buildsmart.vendor.validator.VendorValidator;
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
public class VendorServiceImpl implements VendorService {

    private static final Logger log = LoggerFactory.getLogger(VendorServiceImpl.class);

    private final VendorRepository vendorRepository;
    private final VendorValidator vendorValidator;

    @Override
    public List<VendorDTO> getAllVendors() {
        List<Vendor> vendors = vendorRepository.findAll();
        List<VendorDTO> result = new ArrayList<>();

        for (Vendor vendor : vendors) {
            result.add(toDTO(vendor));
        }
        return result;
    }

    @Override
    public VendorDTO getVendorById(String id) {
        return toDTO(findById(id));
    }

    @Override
    public List<VendorDTO> getVendorsByStatus(Vendor.VendorStatus status) {
        List<Vendor> vendors = vendorRepository.findByStatus(status);
        List<VendorDTO> result = new ArrayList<>();

        for (Vendor vendor : vendors) {
            result.add(toDTO(vendor));
        }
        return result;
    }

    @Override
    public List<VendorDTO> searchVendorsByName(String name) {
        List<Vendor> vendors = vendorRepository.findByNameContainingIgnoreCase(name);
        List<VendorDTO> result = new ArrayList<>();

        for (Vendor vendor : vendors) {
            result.add(toDTO(vendor));
        }
        return result;
    }

    @Override
    public VendorDTO createVendor(VendorDTO dto) {
        log.info("Creating vendor, name={}", dto.getName());
        vendorValidator.validate(dto);
        validateVendorUniqueness(dto, null);

        String lastId = vendorRepository.findTopByOrderByVendorIdDesc()
                .map(Vendor::getVendorId)
                .orElse(null);
        String newId = IdGeneratorUtil.nextVendorId(lastId);

        Vendor vendor = Vendor.builder()
                .vendorId(newId)
                .name(dto.getName())
                .contactInfo(dto.getContactInfo())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .address(dto.getAddress())
                .status(dto.getStatus() != null
                        ? dto.getStatus()
                        : Vendor.VendorStatus.PENDING_APPROVAL)
                .build();

        Vendor savedVendor = vendorRepository.save(vendor);
        return toDTO(savedVendor);
    }

    @Override
    public VendorDTO updateVendor(String id, VendorDTO dto) {
        log.info("Updating vendor id={}", id);
        vendorValidator.validate(dto);
        Vendor v = findById(id);
        validateVendorUniqueness(dto, id);

        v.setName(dto.getName());
        v.setContactInfo(dto.getContactInfo());
        v.setEmail(dto.getEmail());
        v.setPhone(dto.getPhone());
        v.setAddress(dto.getAddress());
        v.setStatus(dto.getStatus());

        Vendor updatedVendor = vendorRepository.save(v);
        return toDTO(updatedVendor);
    }

    @Override
    public VendorDTO updateVendorStatus(String id, Vendor.VendorStatus status) {
        log.info("Updating status of vendor id={} to {}", id, status);
        Vendor v = findById(id);
        v.setStatus(status);

        Vendor updatedVendor = vendorRepository.save(v);
        return toDTO(updatedVendor);
    }

    @Override
    public void deleteVendor(String id) {
        log.info("Deleting vendor id={}", id);
        findById(id); // validates existence
        vendorRepository.deleteById(id);
    }

    @Override
    public PageableResponse<VendorDTO> getAllVendors(int pageNumber, int pageSize, String sortBy, String sortDirection) {
        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(direction, sortBy));
        Page<Vendor> page = vendorRepository.findAll(pageable);
        return convertToPageableResponse(page);
    }

    @Override
    public PageableResponse<VendorDTO> getVendorsByStatus(Vendor.VendorStatus status, int pageNumber, int pageSize, String sortBy, String sortDirection) {
        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(direction, sortBy));
        Page<Vendor> page = vendorRepository.findByStatus(status, pageable);
        return convertToPageableResponse(page);
    }

    @Override
    public PageableResponse<VendorDTO> searchVendorsByName(String name, int pageNumber, int pageSize, String sortBy, String sortDirection) {
        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(direction, sortBy));
        Page<Vendor> page = vendorRepository.findByNameContainingIgnoreCase(name, pageable);
        return convertToPageableResponse(page);
    }

    // ---------- Private helper methods ----------

    private Vendor findById(String id) {
        return vendorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor", id));
    }

    private VendorDTO toDTO(Vendor v) {
        return VendorDTO.builder()
                .vendorId(v.getVendorId())
                .name(v.getName())
                .contactInfo(v.getContactInfo())
                .email(v.getEmail())
                .phone(v.getPhone())
                .address(v.getAddress())
                .status(v.getStatus())
                .build();
    }

    private void validateVendorUniqueness(VendorDTO dto, String currentVendorId) {
        vendorRepository.findByNameIgnoreCase(dto.getName().trim())
                .filter(v -> !v.getVendorId().equals(currentVendorId))
                .ifPresent(v -> {
                    throw new DuplicateResourceException("Vendor name already exists");
                });

        if (dto.getEmail() != null && !dto.getEmail().trim().isEmpty()) {
            vendorRepository.findByEmailIgnoreCase(dto.getEmail().trim())
                    .filter(v -> !v.getVendorId().equals(currentVendorId))
                    .ifPresent(v -> {
                        throw new DuplicateResourceException("Vendor email already exists");
                    });
        }

        if (dto.getPhone() != null && !dto.getPhone().trim().isEmpty()) {
            vendorRepository.findByPhone(dto.getPhone().trim())
                    .filter(v -> !v.getVendorId().equals(currentVendorId))
                    .ifPresent(v -> {
                        throw new DuplicateResourceException("Vendor phone already exists");
                    });
        }
    }

    private PageableResponse<VendorDTO> convertToPageableResponse(Page<Vendor> page) {
        List<VendorDTO> content = new ArrayList<>();
        for (Vendor vendor : page.getContent()) {
            content.add(toDTO(vendor));
        }
        return PageableResponse.<VendorDTO>builder()
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
