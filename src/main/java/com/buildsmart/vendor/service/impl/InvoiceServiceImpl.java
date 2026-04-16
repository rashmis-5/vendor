package com.buildsmart.vendor.service.impl;

import com.buildsmart.vendor.dto.ApprovalRequest;
import com.buildsmart.vendor.dto.InvoiceDTO;
import com.buildsmart.vendor.dto.PageableResponse;
import com.buildsmart.vendor.exception.DuplicateResourceException;
import com.buildsmart.vendor.exception.ResourceNotFoundException;
import com.buildsmart.vendor.model.Contract;
import com.buildsmart.vendor.model.Invoice;
import com.buildsmart.vendor.repository.ContractRepository;
import com.buildsmart.vendor.repository.InvoiceRepository;
import com.buildsmart.vendor.service.FileStorageService;
import com.buildsmart.vendor.service.InvoiceService;
import com.buildsmart.vendor.util.IdGeneratorUtil;
import com.buildsmart.vendor.validator.InvoiceValidator;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private static final Logger log = LoggerFactory.getLogger(InvoiceServiceImpl.class);

    private final InvoiceRepository invoiceRepository;
    private final ContractRepository contractRepository;
    private final FileStorageService fileStorageService;
    private final InvoiceValidator invoiceValidator;

    @Override
    public List<InvoiceDTO> getAllInvoices() {
        List<Invoice> invoices = invoiceRepository.findAll();
        List<InvoiceDTO> result = new ArrayList<>();

        for (Invoice invoice : invoices) {
            result.add(toDTO(invoice));
        }
        return result;
    }

    @Override
    public InvoiceDTO getInvoiceById(String id) {
        return toDTO(findById(id));
    }

    @Override
    public List<InvoiceDTO> getInvoicesByContract(String contractId) {
        List<Invoice> invoices =
                invoiceRepository.findByContractContractId(contractId);
        List<InvoiceDTO> result = new ArrayList<>();

        for (Invoice invoice : invoices) {
            result.add(toDTO(invoice));
        }
        return result;
    }

    @Override
    public List<InvoiceDTO> getInvoicesByStatus(Invoice.InvoiceStatus status) {
        List<Invoice> invoices =
                invoiceRepository.findByStatus(status);
        List<InvoiceDTO> result = new ArrayList<>();

        for (Invoice invoice : invoices) {
            result.add(toDTO(invoice));
        }
        return result;
    }

    @Override
    public InvoiceDTO createInvoice(InvoiceDTO dto) {
        log.info("Creating invoice for contractId={}, amount={}", dto.getContractId(), dto.getAmount());
        invoiceValidator.validate(dto);

        if (invoiceRepository.existsByContractContractIdAndDateAndAmount(
                dto.getContractId(), dto.getDate(), dto.getAmount())) {
            throw new DuplicateResourceException(
                    "Duplicate invoice: same contract, date, and amount already exists");
        }

        Contract contract = contractRepository.findById(dto.getContractId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Contract", dto.getContractId()));

        if (dto.getInvoiceNumber() != null && !dto.getInvoiceNumber().trim().isEmpty() &&
                invoiceRepository.existsByInvoiceNumber(dto.getInvoiceNumber())) {
            throw new DuplicateResourceException(
                    "Invoice number already exists: " + dto.getInvoiceNumber());
        }

        String lastId = invoiceRepository.findTopByOrderByInvoiceIdDesc()
                .map(Invoice::getInvoiceId)
                .orElse(null);
        String newId = IdGeneratorUtil.nextInvoiceId(lastId);

        Invoice invoice = Invoice.builder()
                .invoiceId(newId)
                .contract(contract)
                .amount(dto.getAmount())
                .date(dto.getDate())
                .dueDate(dto.getDueDate())
                .invoiceNumber(dto.getInvoiceNumber())
                .description(dto.getDescription())
                .status(Invoice.InvoiceStatus.DRAFT)
                .submittedBy(dto.getSubmittedBy())
                .build();

        Invoice savedInvoice = invoiceRepository.save(invoice);
        return toDTO(savedInvoice);
    }

    @Override
    public InvoiceDTO updateInvoice(String id, InvoiceDTO dto) {
        log.info("Updating invoice id={}", id);
        invoiceValidator.validate(dto);

        if (invoiceRepository.existsByContractContractIdAndDateAndAmountAndInvoiceIdNot(
                dto.getContractId(), dto.getDate(), dto.getAmount(), id)) {
            throw new DuplicateResourceException(
                    "Duplicate invoice: same contract, date, and amount already exists");
        }

        if (dto.getInvoiceNumber() != null && !dto.getInvoiceNumber().trim().isEmpty()
                && invoiceRepository.existsByInvoiceNumberAndInvoiceIdNot(dto.getInvoiceNumber(), id)) {
            throw new DuplicateResourceException(
                    "Invoice number already exists: " + dto.getInvoiceNumber());
        }

        Invoice invoice = findById(id);

        if (invoice.getStatus() != Invoice.InvoiceStatus.DRAFT) {
            throw new IllegalArgumentException(
                    "Only DRAFT invoices can be updated");
        }

        invoice.setAmount(dto.getAmount());
        invoice.setDate(dto.getDate());
        invoice.setDueDate(dto.getDueDate());
        invoice.setInvoiceNumber(dto.getInvoiceNumber());
        invoice.setDescription(dto.getDescription());

        Invoice updatedInvoice = invoiceRepository.save(invoice);
        return toDTO(updatedInvoice);
    }

    @Override
    public InvoiceDTO uploadInvoiceFile(String id,
                                        MultipartFile file,
                                        String submittedBy) throws IOException {
        log.info("Uploading file for invoice id={}, submittedBy={}", id, submittedBy);
        Invoice invoice = findById(id);

        String filePath = fileStorageService.storeFile(file, "invoices");

        invoice.setInvoiceFilePath(filePath);
        invoice.setInvoiceFileName(file.getOriginalFilename());
        invoice.setInvoiceFileType(file.getContentType());
        invoice.setStatus(Invoice.InvoiceStatus.SUBMITTED);
        invoice.setSubmittedBy(submittedBy);
        invoice.setSubmittedAt(LocalDateTime.now());

        Invoice updatedInvoice = invoiceRepository.save(invoice);
        return toDTO(updatedInvoice);
    }

    @Override
    public InvoiceDTO processApproval(String id, ApprovalRequest request) {
        log.info("Processing approval for invoice id={}, approved={}", id, request.isApproved());
        Invoice invoice = findById(id);

        if (invoice.getStatus() != Invoice.InvoiceStatus.SUBMITTED &&
                invoice.getStatus() != Invoice.InvoiceStatus.UNDER_REVIEW) {
            throw new IllegalArgumentException(
                    "Invoice must be SUBMITTED or UNDER_REVIEW for approval");
        }

        if (request.isApproved()) {
            invoice.setStatus(Invoice.InvoiceStatus.APPROVED);
            invoice.setApprovedBy(request.getApprovedBy());
            invoice.setApprovedAt(LocalDateTime.now());
        } else {
            invoice.setStatus(Invoice.InvoiceStatus.REJECTED);
            invoice.setRejectedBy(request.getRejectedBy());
            invoice.setRejectedAt(LocalDateTime.now());
            invoice.setRejectionReason(request.getRejectionReason());
        }

        return toDTO(invoiceRepository.save(invoice));
    }

    @Override
    public InvoiceDTO markAsPaid(String id) {
        log.info("Marking invoice id={} as paid", id);
        Invoice invoice = findById(id);

        if (invoice.getStatus() != Invoice.InvoiceStatus.APPROVED) {
            throw new IllegalArgumentException(
                    "Only APPROVED invoices can be marked as paid");
        }

        invoice.setStatus(Invoice.InvoiceStatus.PAID);
        return toDTO(invoiceRepository.save(invoice));
    }

    @Override
    public InvoiceDTO updateInvoiceStatus(String id, Invoice.InvoiceStatus status) {
        log.info("Updating invoice id={} status to {}", id, status);
        Invoice invoice = findById(id);

        if (status == null) {
            throw new IllegalArgumentException("Invoice status cannot be null");
        }

        // Validate status transitions
        Invoice.InvoiceStatus currentStatus = invoice.getStatus();

        if (status.equals(currentStatus)) {
            throw new IllegalArgumentException(
                    "Invoice is already in " + status + " status");
        }

        // Define allowed transitions
        boolean validTransition = false;
        switch (currentStatus) {
            case DRAFT:
                validTransition = status == Invoice.InvoiceStatus.SUBMITTED || 
                                status == Invoice.InvoiceStatus.CANCELLED;
                break;
            case SUBMITTED:
                validTransition = status == Invoice.InvoiceStatus.UNDER_REVIEW || 
                                status == Invoice.InvoiceStatus.DRAFT ||
                                status == Invoice.InvoiceStatus.CANCELLED;
                break;
            case UNDER_REVIEW:
                validTransition = status == Invoice.InvoiceStatus.APPROVED || 
                                status == Invoice.InvoiceStatus.REJECTED;
                break;
            case APPROVED:
                validTransition = status == Invoice.InvoiceStatus.PAID || 
                                status == Invoice.InvoiceStatus.CANCELLED;
                break;
            case REJECTED:
                validTransition = status == Invoice.InvoiceStatus.DRAFT || 
                                status == Invoice.InvoiceStatus.CANCELLED;
                break;
            case PAID:
                validTransition = status == Invoice.InvoiceStatus.CANCELLED;
                break;
            case CANCELLED:
                validTransition = false;
                break;
        }

        if (!validTransition) {
            throw new IllegalArgumentException(
                    "Cannot transition from " + currentStatus + " to " + status);
        }

        invoice.setStatus(status);
        return toDTO(invoiceRepository.save(invoice));
    }

    @Override
    public void deleteInvoice(String id) {
        log.info("Deleting invoice id={}", id);
        Invoice invoice = findById(id);

        if (invoice.getInvoiceFilePath() != null) {
            fileStorageService.deleteFile(invoice.getInvoiceFilePath());
        }

        invoiceRepository.deleteById(id);
    }

    @Override
    public PageableResponse<InvoiceDTO> getAllInvoices(int pageNumber, int pageSize, String sortBy, String sortDirection) {
        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(direction, sortBy));
        Page<Invoice> page = invoiceRepository.findAll(pageable);
        return convertToPageableResponse(page);
    }

    @Override
    public PageableResponse<InvoiceDTO> getInvoicesByContract(String contractId, int pageNumber, int pageSize, String sortBy, String sortDirection) {
        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(direction, sortBy));
        Page<Invoice> page = invoiceRepository.findByContractContractId(contractId, pageable);
        return convertToPageableResponse(page);
    }

    @Override
    public PageableResponse<InvoiceDTO> getInvoicesByStatus(Invoice.InvoiceStatus status, int pageNumber, int pageSize, String sortBy, String sortDirection) {
        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(direction, sortBy));
        Page<Invoice> page = invoiceRepository.findByStatus(status, pageable);
        return convertToPageableResponse(page);
    }

    // ---------- Private helper methods ----------

    private Invoice findById(String id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Invoice", id));
    }

    private InvoiceDTO toDTO(Invoice i) {
        return InvoiceDTO.builder()
                .invoiceId(i.getInvoiceId())
                .contractId(i.getContract().getContractId())
                .amount(i.getAmount())
                .date(i.getDate())
                .dueDate(i.getDueDate())
                .invoiceNumber(i.getInvoiceNumber())
                .description(i.getDescription())
                .invoiceFilePath(i.getInvoiceFilePath())
                .invoiceFileName(i.getInvoiceFileName())
                .invoiceFileType(i.getInvoiceFileType())
                .status(i.getStatus())
                .approvedBy(i.getApprovedBy())
                .approvedAt(i.getApprovedAt())
                .rejectedBy(i.getRejectedBy())
                .rejectedAt(i.getRejectedAt())
                .rejectionReason(i.getRejectionReason())
                .submittedBy(i.getSubmittedBy())
                .submittedAt(i.getSubmittedAt())
                .build();
    }

    private PageableResponse<InvoiceDTO> convertToPageableResponse(Page<Invoice> page) {
        List<InvoiceDTO> content = new ArrayList<>();
        for (Invoice invoice : page.getContent()) {
            content.add(toDTO(invoice));
        }
        return PageableResponse.<InvoiceDTO>builder()
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

