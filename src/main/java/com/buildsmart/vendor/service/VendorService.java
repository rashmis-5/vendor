package com.buildsmart.vendor.service;
import com.buildsmart.vendor.dto.VendorDTO;
import com.buildsmart.vendor.exception.ResourceNotFoundException;
import com.buildsmart.vendor.model.Vendor;
import com.buildsmart.vendor.repository.VendorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class VendorService {
    private final VendorRepository vendorRepository;
    public List<VendorDTO> getAllVendors() {
        return vendorRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }
    public VendorDTO getVendorById(Long id) { return toDTO(findById(id)); }
    public List<VendorDTO> getVendorsByStatus(Vendor.VendorStatus status) {
        return vendorRepository.findByStatus(status).stream().map(this::toDTO).collect(Collectors.toList());
    }
    public List<VendorDTO> searchVendorsByName(String name) {
        return vendorRepository.findByNameContainingIgnoreCase(name).stream().map(this::toDTO).collect(Collectors.toList());
    }
    public VendorDTO createVendor(VendorDTO dto) {
        Vendor vendor = Vendor.builder().name(dto.getName()).contactInfo(dto.getContactInfo())
                .email(dto.getEmail()).phone(dto.getPhone()).address(dto.getAddress())
                .status(dto.getStatus() != null ? dto.getStatus() : Vendor.VendorStatus.PENDING_APPROVAL).build();
        return toDTO(vendorRepository.save(vendor));
    }
    public VendorDTO updateVendor(Long id, VendorDTO dto) {
        Vendor v = findById(id);
        v.setName(dto.getName()); v.setContactInfo(dto.getContactInfo());
        v.setEmail(dto.getEmail()); v.setPhone(dto.getPhone());
        v.setAddress(dto.getAddress()); v.setStatus(dto.getStatus());
        return toDTO(vendorRepository.save(v));
    }
    public VendorDTO updateVendorStatus(Long id, Vendor.VendorStatus status) {
        Vendor v = findById(id); v.setStatus(status); return toDTO(vendorRepository.save(v));
    }
    public void deleteVendor(Long id) { findById(id); vendorRepository.deleteById(id); }
    private Vendor findById(Long id) {
        return vendorRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Vendor", id));
    }
    public VendorDTO toDTO(Vendor v) {
        return VendorDTO.builder().vendorId(v.getVendorId()).name(v.getName())
                .contactInfo(v.getContactInfo()).email(v.getEmail()).phone(v.getPhone())
                .address(v.getAddress()).status(v.getStatus()).build();
    }
}
