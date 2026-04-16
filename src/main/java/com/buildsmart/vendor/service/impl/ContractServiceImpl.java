package com.buildsmart.vendor.service.impl;

import com.buildsmart.vendor.dto.ContractDTO;
import com.buildsmart.vendor.dto.PageableResponse;
import com.buildsmart.vendor.exception.DuplicateResourceException;
import com.buildsmart.vendor.exception.ResourceNotFoundException;
import com.buildsmart.vendor.model.Contract;
import com.buildsmart.vendor.model.Project;
import com.buildsmart.vendor.model.Vendor;
import com.buildsmart.vendor.repository.ContractRepository;
import com.buildsmart.vendor.repository.ProjectRepository;
import com.buildsmart.vendor.repository.VendorRepository;
import com.buildsmart.vendor.service.ContractService;
import com.buildsmart.vendor.util.IdGeneratorUtil;
import com.buildsmart.vendor.validator.ContractValidator;
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
public class ContractServiceImpl implements ContractService {

    private static final Logger log = LoggerFactory.getLogger(ContractServiceImpl.class);

    private final ContractRepository contractRepository;
    private final VendorRepository vendorRepository;
    private final ProjectRepository projectRepository;
    private final ContractValidator contractValidator;



    @Override
    public List<ContractDTO> getAllContracts() {
        List<Contract> contracts = contractRepository.findAll();
        List<ContractDTO> result = new ArrayList<>();

        for (Contract contract : contracts) {
            result.add(toDTO(contract));
        }
        return result;
    }

    @Override
    public ContractDTO getContractById(String id) {
        return toDTO(findById(id));
    }

    @Override
    public List<ContractDTO> getContractsByVendor(String vendorId) {
        List<Contract> contracts =
                contractRepository.findByVendorVendorId(vendorId);
        List<ContractDTO> result = new ArrayList<>();

        for (Contract contract : contracts) {
            result.add(toDTO(contract));
        }
        return result;
    }

    @Override
    public List<ContractDTO> getContractsByProject(String projectId) {
        List<Contract> contracts =
                contractRepository.findByProjectProjectId(projectId);
        List<ContractDTO> result = new ArrayList<>();

        for (Contract contract : contracts) {
            result.add(toDTO(contract));
        }
        return result;
    }

    @Override
    public List<ContractDTO> getContractsByStatus(Contract.ContractStatus status) {
        List<Contract> contracts =
                contractRepository.findByStatus(status);
        List<ContractDTO> result = new ArrayList<>();

        for (Contract contract : contracts) {
            result.add(toDTO(contract));
        }
        return result;
    }

    @Override
    public ContractDTO createContract(ContractDTO dto) {
        log.info("Creating contract for vendorId={}, projectId={}", dto.getVendorId(), dto.getProjectId());
        contractValidator.validate(dto);

        if (contractRepository.existsByVendorVendorIdAndProjectProjectIdAndStartDateAndEndDate(
                dto.getVendorId(), dto.getProjectId(), dto.getStartDate(), dto.getEndDate())) {
            throw new DuplicateResourceException(
                    "Contract already exists for this vendor, project, and date range");
        }

        Vendor vendor = vendorRepository.findById(dto.getVendorId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Vendor", dto.getVendorId()));

        Project project = projectRepository.findById(dto.getProjectId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Project", dto.getProjectId()));

        String lastId = contractRepository.findTopByOrderByContractIdDesc()
                .map(Contract::getContractId)
                .orElse(null);
        String newId = IdGeneratorUtil.nextContractId(lastId);

        Contract contract = Contract.builder()
                .contractId(newId)
                .vendor(vendor)
                .project(project)
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .value(dto.getValue())
                .description(dto.getDescription())
                .terms(dto.getTerms())
                .status(dto.getStatus() != null
                        ? dto.getStatus()
                        : Contract.ContractStatus.DRAFT)
                .build();

        Contract savedContract = contractRepository.save(contract);
        return toDTO(savedContract);
    }

    @Override
    public ContractDTO updateContract(String id, ContractDTO dto) {
        log.info("Updating contract id={}", id);
        contractValidator.validate(dto);

        if (contractRepository.existsByVendorVendorIdAndProjectProjectIdAndStartDateAndEndDateAndContractIdNot(
                dto.getVendorId(), dto.getProjectId(), dto.getStartDate(), dto.getEndDate(), id)) {
            throw new DuplicateResourceException(
                    "Contract already exists for this vendor, project, and date range");
        }

        Contract contract = findById(id);

        Vendor vendor = vendorRepository.findById(dto.getVendorId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Vendor", dto.getVendorId()));

        Project project = projectRepository.findById(dto.getProjectId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Project", dto.getProjectId()));

        contract.setVendor(vendor);
        contract.setProject(project);
        contract.setStartDate(dto.getStartDate());
        contract.setEndDate(dto.getEndDate());
        contract.setValue(dto.getValue());
        contract.setDescription(dto.getDescription());
        contract.setTerms(dto.getTerms());
        contract.setStatus(dto.getStatus());

        Contract updatedContract = contractRepository.save(contract);
        return toDTO(updatedContract);
    }

    @Override
    public ContractDTO updateContractStatus(String id, Contract.ContractStatus status) {
        log.info("Updating status of contract id={} to {}", id, status);
        Contract contract = findById(id);
        contract.setStatus(status);

        Contract updatedContract = contractRepository.save(contract);
        return toDTO(updatedContract);
    }

    @Override
    public void deleteContract(String id) {
        log.info("Deleting contract id={}", id);
        findById(id); // validate existence
        contractRepository.deleteById(id);
    }

    @Override
    public PageableResponse<ContractDTO> getAllContracts(int pageNumber, int pageSize, String sortBy, String sortDirection) {
        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(direction, sortBy));
        Page<Contract> page = contractRepository.findAll(pageable);
        return convertToPageableResponse(page);
    }

    @Override
    public PageableResponse<ContractDTO> getContractsByVendor(String vendorId, int pageNumber, int pageSize, String sortBy, String sortDirection) {
        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(direction, sortBy));
        Page<Contract> page = contractRepository.findByVendorVendorId(vendorId, pageable);
        return convertToPageableResponse(page);
    }

    @Override
    public PageableResponse<ContractDTO> getContractsByProject(String projectId, int pageNumber, int pageSize, String sortBy, String sortDirection) {
        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(direction, sortBy));
        Page<Contract> page = contractRepository.findByProjectProjectId(projectId, pageable);
        return convertToPageableResponse(page);
    }

    @Override
    public PageableResponse<ContractDTO> getContractsByStatus(Contract.ContractStatus status, int pageNumber, int pageSize, String sortBy, String sortDirection) {
        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(direction, sortBy));
        Page<Contract> page = contractRepository.findByStatus(status, pageable);
        return convertToPageableResponse(page);
    }

    // ---------- Private helper methods ----------

    private Contract findById(String id) {
        return contractRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Contract", id));
    }

    private ContractDTO toDTO(Contract c) {
        return ContractDTO.builder()
                .contractId(c.getContractId())
                .vendorId(c.getVendor().getVendorId())
                .vendorName(c.getVendor().getName())
                .projectId(c.getProject().getProjectId())
                .projectName(c.getProject().getProjectName())
                .startDate(c.getStartDate())
                .endDate(c.getEndDate())
                .value(c.getValue())
                .description(c.getDescription())
                .terms(c.getTerms())
                .status(c.getStatus())
                .build();
    }

    private PageableResponse<ContractDTO> convertToPageableResponse(Page<Contract> page) {
        List<ContractDTO> content = new ArrayList<>();
        for (Contract contract : page.getContent()) {
            content.add(toDTO(contract));
        }
        return PageableResponse.<ContractDTO>builder()
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
