package com.buildsmart.vendor.service;
import com.buildsmart.vendor.dto.ApprovalRequest;
import com.buildsmart.vendor.dto.VendorDocumentDTO;
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
public class VendorDocumentService {
    private final VendorDocumentRepository documentRepository;
    private final ContractRepository contractRepository;
    private final FileStorageService fileStorageService;
    public List<VendorDocumentDTO> getAllDocuments() {
        return documentRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }
    public VendorDocumentDTO getDocumentById(Long id) { return toDTO(findById(id)); }
    public List<VendorDocumentDTO> getDocumentsByContract(Long contractId) {
        return documentRepository.findByContractContractId(contractId).stream().map(this::toDTO).collect(Collectors.toList());
    }
    public List<VendorDocumentDTO> getDocumentsByStatus(VendorDocument.DocumentStatus status) {
        return documentRepository.findByStatus(status).stream().map(this::toDTO).collect(Collectors.toList());
    }
    public List<VendorDocumentDTO> getDocumentsByType(VendorDocument.DocumentType type) {
        return documentRepository.findByDocumentType(type).stream().map(this::toDTO).collect(Collectors.toList());
    }
    public VendorDocumentDTO uploadDocument(Long contractId, MultipartFile file,
            VendorDocument.DocumentType documentType, String uploadedBy, String description) throws IOException {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new ResourceNotFoundException("Contract", contractId));
        String filePath = fileStorageService.storeFile(file, "documents/" + documentType.name().toLowerCase());
        VendorDocument document = VendorDocument.builder().contract(contract)
                .documentName(file.getOriginalFilename()).filePath(filePath)
                .fileType(file.getContentType()).fileSize(file.getSize())
                .documentType(documentType).status(VendorDocument.DocumentStatus.SUBMITTED)
                .uploadedBy(uploadedBy).uploadedAt(LocalDateTime.now()).description(description).build();
        return toDTO(documentRepository.save(document));
    }
    public VendorDocumentDTO processApproval(Long id, ApprovalRequest request) {
        VendorDocument doc = findById(id);
        if (doc.getStatus() != VendorDocument.DocumentStatus.SUBMITTED && doc.getStatus() != VendorDocument.DocumentStatus.UNDER_REVIEW)
            throw new IllegalArgumentException("Document must be SUBMITTED or UNDER_REVIEW for approval");
        if (request.isApproved()) {
            doc.setStatus(VendorDocument.DocumentStatus.APPROVED);
            doc.setApprovedBy(request.getApprovedBy()); doc.setApprovedAt(LocalDateTime.now());
        } else {
            doc.setStatus(VendorDocument.DocumentStatus.REJECTED);
            doc.setRejectionReason(request.getRejectionReason());
        }
        return toDTO(documentRepository.save(doc));
    }
    public void deleteDocument(Long id) {
        VendorDocument doc = findById(id);
        fileStorageService.deleteFile(doc.getFilePath());
        documentRepository.deleteById(id);
    }
    private VendorDocument findById(Long id) {
        return documentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Document", id));
    }
    private VendorDocumentDTO toDTO(VendorDocument d) {
        return VendorDocumentDTO.builder().documentId(d.getDocumentId()).contractId(d.getContract().getContractId())
                .documentName(d.getDocumentName()).filePath(d.getFilePath()).fileType(d.getFileType())
                .fileSize(d.getFileSize()).documentType(d.getDocumentType()).status(d.getStatus())
                .uploadedBy(d.getUploadedBy()).uploadedAt(d.getUploadedAt()).approvedBy(d.getApprovedBy())
                .approvedAt(d.getApprovedAt()).rejectionReason(d.getRejectionReason()).description(d.getDescription()).build();
    }
}
