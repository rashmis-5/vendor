package com.buildsmart.vendor.service.impl;

import com.buildsmart.vendor.dto.ApprovalRequest;
import com.buildsmart.vendor.dto.PageableResponse;
import com.buildsmart.vendor.dto.VendorDocumentDTO;
import com.buildsmart.vendor.exception.DuplicateResourceException;
import com.buildsmart.vendor.exception.ResourceNotFoundException;
import com.buildsmart.vendor.model.Contract;
import com.buildsmart.vendor.model.VendorDocument;
import com.buildsmart.vendor.repository.ContractRepository;
import com.buildsmart.vendor.repository.VendorDocumentRepository;
import com.buildsmart.vendor.service.FileStorageService;
import com.buildsmart.vendor.service.VendorDocumentService;
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
public class VendorDocumentServiceImpl implements VendorDocumentService {

    private static final Logger log = LoggerFactory.getLogger(VendorDocumentServiceImpl.class);

    private final VendorDocumentRepository documentRepository;
    private final ContractRepository contractRepository;
    private final FileStorageService fileStorageService;

    @Override
    public List<VendorDocumentDTO> getAllDocuments() {
        List<VendorDocument> documents = documentRepository.findAll();
        List<VendorDocumentDTO> result = new ArrayList<>();

        for (VendorDocument document : documents) {
            result.add(toDTO(document));
        }
        return result;
    }

    @Override
    public VendorDocumentDTO getDocumentById(Long id) {
        return toDTO(findById(id));
    }

    @Override
    public List<VendorDocumentDTO> getDocumentsByContract(String contractId) {
        List<VendorDocument> documents =
                documentRepository.findByContractContractId(contractId);
        List<VendorDocumentDTO> result = new ArrayList<>();

        for (VendorDocument document : documents) {
            result.add(toDTO(document));
        }
        return result;
    }

    @Override
    public List<VendorDocumentDTO> getDocumentsByStatus(VendorDocument.DocumentStatus status) {
        List<VendorDocument> documents =
                documentRepository.findByStatus(status);
        List<VendorDocumentDTO> result = new ArrayList<>();

        for (VendorDocument document : documents) {
            result.add(toDTO(document));
        }
        return result;
    }

    @Override
    public List<VendorDocumentDTO> getDocumentsByType(VendorDocument.DocumentType type) {
        List<VendorDocument> documents =
                documentRepository.findByDocumentType(type);
        List<VendorDocumentDTO> result = new ArrayList<>();

        for (VendorDocument document : documents) {
            result.add(toDTO(document));
        }
        return result;
    }

    @Override
    public VendorDocumentDTO uploadDocument(
            String contractId,
            MultipartFile file,
            VendorDocument.DocumentType documentType,
            String uploadedBy,
            String description) throws IOException {
        log.info("Uploading document for contractId={}, type={}, uploadedBy={}", contractId, documentType, uploadedBy);

        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Contract", contractId));

        if (documentRepository.existsByContractContractIdAndDocumentNameIgnoreCaseAndDocumentType(
                contractId,
                file.getOriginalFilename(),
                documentType)) {
            throw new DuplicateResourceException(
                    "Document with same name and type already exists for this contract");
        }

        String filePath = fileStorageService.storeFile(
                file,
                "documents/" + documentType.name().toLowerCase()
        );

        VendorDocument document = VendorDocument.builder()
                .contract(contract)
                .documentName(file.getOriginalFilename())
                .filePath(filePath)
                .fileType(file.getContentType())
                .fileSize(file.getSize())
                .documentType(documentType)
                .status(VendorDocument.DocumentStatus.SUBMITTED)
                .uploadedBy(uploadedBy)
                .uploadedAt(LocalDateTime.now())
                .description(description)
                .build();

        VendorDocument savedDocument = documentRepository.save(document);
        return toDTO(savedDocument);
    }

    @Override
    public VendorDocumentDTO processApproval(Long id, ApprovalRequest request) {
        log.info("Processing approval for document id={}, approved={}", id, request.isApproved());
        VendorDocument doc = findById(id);

        if (doc.getStatus() != VendorDocument.DocumentStatus.SUBMITTED &&
                doc.getStatus() != VendorDocument.DocumentStatus.UNDER_REVIEW) {
            throw new IllegalArgumentException(
                    "Document must be SUBMITTED or UNDER_REVIEW for approval");
        }

        if (request.isApproved()) {
            doc.setStatus(VendorDocument.DocumentStatus.APPROVED);
            doc.setApprovedBy(request.getApprovedBy());
            doc.setApprovedAt(LocalDateTime.now());
        } else {
            doc.setStatus(VendorDocument.DocumentStatus.REJECTED);
            doc.setRejectedBy(request.getRejectedBy());
            doc.setRejectedAt(LocalDateTime.now());
            doc.setRejectionReason(request.getRejectionReason());
        }

        VendorDocument updatedDoc = documentRepository.save(doc);
        return toDTO(updatedDoc);
    }

    @Override
    public void deleteDocument(Long id) {
        log.info("Deleting document id={}", id);
        VendorDocument doc = findById(id);
        fileStorageService.deleteFile(doc.getFilePath());
        documentRepository.deleteById(id);
    }

    @Override
    public PageableResponse<VendorDocumentDTO> getAllDocuments(int pageNumber, int pageSize, String sortBy, String sortDirection) {
        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(direction, sortBy));
        Page<VendorDocument> page = documentRepository.findAll(pageable);
        return convertToPageableResponse(page);
    }

    @Override
    public PageableResponse<VendorDocumentDTO> getDocumentsByContract(String contractId, int pageNumber, int pageSize, String sortBy, String sortDirection) {
        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(direction, sortBy));
        Page<VendorDocument> page = documentRepository.findByContractContractId(contractId, pageable);
        return convertToPageableResponse(page);
    }

    @Override
    public PageableResponse<VendorDocumentDTO> getDocumentsByStatus(VendorDocument.DocumentStatus status, int pageNumber, int pageSize, String sortBy, String sortDirection) {
        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(direction, sortBy));
        Page<VendorDocument> page = documentRepository.findByStatus(status, pageable);
        return convertToPageableResponse(page);
    }

    @Override
    public PageableResponse<VendorDocumentDTO> getDocumentsByType(VendorDocument.DocumentType type, int pageNumber, int pageSize, String sortBy, String sortDirection) {
        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(direction, sortBy));
        Page<VendorDocument> page = documentRepository.findByDocumentType(type, pageable);
        return convertToPageableResponse(page);
    }

    private PageableResponse<VendorDocumentDTO> convertToPageableResponse(Page<VendorDocument> page) {
        List<VendorDocumentDTO> content = new ArrayList<>();
        for (VendorDocument doc : page.getContent()) {
            content.add(toDTO(doc));
        }
        return PageableResponse.<VendorDocumentDTO>builder()
                .content(content)
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .isFirstPage(page.isFirst())
                .isLastPage(page.isLast())
                .build();
    }

    // ---------- Private helper methods ----------

    private VendorDocument findById(Long id) {
        return documentRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Document", id));
    }

    private VendorDocumentDTO toDTO(VendorDocument d) {
        return VendorDocumentDTO.builder()
                .documentId(d.getDocumentId())
                .contractId(d.getContract().getContractId())
                .documentName(d.getDocumentName())
                .filePath(d.getFilePath())
                .fileType(d.getFileType())
                .fileSize(d.getFileSize())
                .documentType(d.getDocumentType())
                .status(d.getStatus())
                .uploadedBy(d.getUploadedBy())
                .uploadedAt(d.getUploadedAt())
                .approvedBy(d.getApprovedBy())
                .approvedAt(d.getApprovedAt())
                .rejectedBy(d.getRejectedBy())
                .rejectedAt(d.getRejectedAt())
                .rejectionReason(d.getRejectionReason())
                .description(d.getDescription())
                .build();
    }
}
