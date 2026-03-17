package com.buildsmart.vendor.service;
import com.buildsmart.vendor.dto.ContractDTO;
import com.buildsmart.vendor.exception.ResourceNotFoundException;
import com.buildsmart.vendor.model.*;
import com.buildsmart.vendor.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class ContractService {
    private final ContractRepository contractRepository;
    private final VendorRepository vendorRepository;
    public List<ContractDTO> getAllContracts() {
        return contractRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }
    public ContractDTO getContractById(Long id) { return toDTO(findById(id)); }
    public List<ContractDTO> getContractsByVendor(Long vendorId) {
        return contractRepository.findByVendorVendorId(vendorId).stream().map(this::toDTO).collect(Collectors.toList());
    }
    public List<ContractDTO> getContractsByProject(String projectId) {
        return contractRepository.findByProjectId(projectId).stream().map(this::toDTO).collect(Collectors.toList());
    }
    public List<ContractDTO> getContractsByStatus(Contract.ContractStatus status) {
        return contractRepository.findByStatus(status).stream().map(this::toDTO).collect(Collectors.toList());
    }
    public ContractDTO createContract(ContractDTO dto) {
        Vendor vendor = vendorRepository.findById(dto.getVendorId())
                .orElseThrow(() -> new ResourceNotFoundException("Vendor", dto.getVendorId()));
        Contract c = Contract.builder().vendor(vendor).projectId(dto.getProjectId())
                .startDate(dto.getStartDate()).endDate(dto.getEndDate()).value(dto.getValue())
                .description(dto.getDescription()).terms(dto.getTerms())
                .status(dto.getStatus() != null ? dto.getStatus() : Contract.ContractStatus.DRAFT).build();
        return toDTO(contractRepository.save(c));
    }
    public ContractDTO updateContract(Long id, ContractDTO dto) {
        Contract c = findById(id);
        Vendor vendor = vendorRepository.findById(dto.getVendorId())
                .orElseThrow(() -> new ResourceNotFoundException("Vendor", dto.getVendorId()));
        c.setVendor(vendor); c.setProjectId(dto.getProjectId());
        c.setStartDate(dto.getStartDate()); c.setEndDate(dto.getEndDate());
        c.setValue(dto.getValue()); c.setDescription(dto.getDescription());
        c.setTerms(dto.getTerms()); c.setStatus(dto.getStatus());
        return toDTO(contractRepository.save(c));
    }
    public ContractDTO updateContractStatus(Long id, Contract.ContractStatus status) {
        Contract c = findById(id); c.setStatus(status); return toDTO(contractRepository.save(c));
    }
    public void deleteContract(Long id) { findById(id); contractRepository.deleteById(id); }
    private Contract findById(Long id) {
        return contractRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Contract", id));
    }
    public ContractDTO toDTO(Contract c) {
        return ContractDTO.builder().contractId(c.getContractId())
                .vendorId(c.getVendor().getVendorId()).vendorName(c.getVendor().getName())
                .projectId(c.getProjectId()).startDate(c.getStartDate()).endDate(c.getEndDate())
                .value(c.getValue()).description(c.getDescription()).terms(c.getTerms()).status(c.getStatus()).build();
    }
}
