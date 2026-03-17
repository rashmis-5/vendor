package com.buildsmart.vendor.service;
import com.buildsmart.vendor.dto.ApprovalRequest;
import com.buildsmart.vendor.dto.InvoiceDTO;
import com.buildsmart.vendor.exception.ResourceNotFoundException;
import com.buildsmart.vendor.model.*;
import com.buildsmart.vendor.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class InvoiceService {
    private final InvoiceRepository invoiceRepository;
    private final ContractRepository contractRepository;
    private final FileStorageService fileStorageService;
    public List<InvoiceDTO> getAllInvoices() {
        return invoiceRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }
    public InvoiceDTO getInvoiceById(Long id) { return toDTO(findById(id)); }
    public List<InvoiceDTO> getInvoicesByContract(Long contractId) {
        return invoiceRepository.findByContractContractId(contractId).stream().map(this::toDTO).collect(Collectors.toList());
    }
    public List<InvoiceDTO> getInvoicesByStatus(Invoice.InvoiceStatus status) {
        return invoiceRepository.findByStatus(status).stream().map(this::toDTO).collect(Collectors.toList());
    }
    public InvoiceDTO createInvoice(InvoiceDTO dto) {
        Contract contract = contractRepository.findById(dto.getContractId())
                .orElseThrow(() -> new ResourceNotFoundException("Contract", dto.getContractId()));
        if (dto.getInvoiceNumber() != null && invoiceRepository.existsByInvoiceNumber(dto.getInvoiceNumber()))
            throw new IllegalArgumentException("Invoice number already exists: " + dto.getInvoiceNumber());
        Invoice invoice = Invoice.builder().contract(contract).amount(dto.getAmount())
                .date(dto.getDate()).dueDate(dto.getDueDate()).invoiceNumber(dto.getInvoiceNumber())
                .description(dto.getDescription()).status(Invoice.InvoiceStatus.DRAFT)
                .submittedBy(dto.getSubmittedBy()).build();
        return toDTO(invoiceRepository.save(invoice));
    }
    public InvoiceDTO updateInvoice(Long id, InvoiceDTO dto) {
        Invoice i = findById(id);
        if (i.getStatus() != Invoice.InvoiceStatus.DRAFT)
            throw new IllegalArgumentException("Only DRAFT invoices can be updated");
        i.setAmount(dto.getAmount()); i.setDate(dto.getDate());
        i.setDueDate(dto.getDueDate()); i.setDescription(dto.getDescription());
        return toDTO(invoiceRepository.save(i));
    }
    public InvoiceDTO uploadInvoiceFile(Long id, MultipartFile file, String submittedBy) throws IOException {
        Invoice invoice = findById(id);
        String filePath = fileStorageService.storeFile(file, "invoices");
        invoice.setInvoiceFilePath(filePath); invoice.setInvoiceFileName(file.getOriginalFilename());
        invoice.setInvoiceFileType(file.getContentType()); invoice.setStatus(Invoice.InvoiceStatus.SUBMITTED);
        invoice.setSubmittedBy(submittedBy); invoice.setSubmittedAt(LocalDateTime.now());
        return toDTO(invoiceRepository.save(invoice));
    }
    public InvoiceDTO processApproval(Long id, ApprovalRequest request) {
        Invoice invoice = findById(id);
        if (invoice.getStatus() != Invoice.InvoiceStatus.SUBMITTED && invoice.getStatus() != Invoice.InvoiceStatus.UNDER_REVIEW)
            throw new IllegalArgumentException("Invoice must be SUBMITTED or UNDER_REVIEW for approval");
        if (request.isApproved()) {
            invoice.setStatus(Invoice.InvoiceStatus.APPROVED);
            invoice.setApprovedBy(request.getApprovedBy()); invoice.setApprovedAt(LocalDateTime.now());
        } else {
            invoice.setStatus(Invoice.InvoiceStatus.REJECTED);
            invoice.setRejectionReason(request.getRejectionReason());
        }
        return toDTO(invoiceRepository.save(invoice));
    }
    public InvoiceDTO markAsPaid(Long id) {
        Invoice invoice = findById(id);
        if (invoice.getStatus() != Invoice.InvoiceStatus.APPROVED)
            throw new IllegalArgumentException("Only APPROVED invoices can be marked as paid");
        invoice.setStatus(Invoice.InvoiceStatus.PAID);
        return toDTO(invoiceRepository.save(invoice));
    }
    public void deleteInvoice(Long id) {
        Invoice invoice = findById(id);
        if (invoice.getInvoiceFilePath() != null) fileStorageService.deleteFile(invoice.getInvoiceFilePath());
        invoiceRepository.deleteById(id);
    }
    private Invoice findById(Long id) {
        return invoiceRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Invoice", id));
    }
    private InvoiceDTO toDTO(Invoice i) {
        return InvoiceDTO.builder().invoiceId(i.getInvoiceId()).contractId(i.getContract().getContractId())
                .amount(i.getAmount()).date(i.getDate()).dueDate(i.getDueDate())
                .invoiceNumber(i.getInvoiceNumber()).description(i.getDescription())
                .invoiceFilePath(i.getInvoiceFilePath()).invoiceFileName(i.getInvoiceFileName())
                .invoiceFileType(i.getInvoiceFileType()).status(i.getStatus())
                .approvedBy(i.getApprovedBy()).approvedAt(i.getApprovedAt())
                .rejectionReason(i.getRejectionReason()).submittedBy(i.getSubmittedBy())
                .submittedAt(i.getSubmittedAt()).build();
    }
}
