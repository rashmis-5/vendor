package com.buildsmart.vendor.service;

import com.buildsmart.vendor.dto.ApprovalRequest;
import com.buildsmart.vendor.dto.PageableResponse;
import com.buildsmart.vendor.dto.VendorDocumentDTO;
import com.buildsmart.vendor.model.VendorDocument;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface VendorDocumentService {

    List<VendorDocumentDTO> getAllDocuments();

    PageableResponse<VendorDocumentDTO> getAllDocuments(int pageNumber, int pageSize, String sortBy, String sortDirection);

    VendorDocumentDTO getDocumentById(Long id);

    List<VendorDocumentDTO> getDocumentsByContract(String contractId);

    PageableResponse<VendorDocumentDTO> getDocumentsByContract(String contractId, int pageNumber, int pageSize, String sortBy, String sortDirection);

    List<VendorDocumentDTO> getDocumentsByStatus(VendorDocument.DocumentStatus status);

    PageableResponse<VendorDocumentDTO> getDocumentsByStatus(VendorDocument.DocumentStatus status, int pageNumber, int pageSize, String sortBy, String sortDirection);

    List<VendorDocumentDTO> getDocumentsByType(VendorDocument.DocumentType type);

    PageableResponse<VendorDocumentDTO> getDocumentsByType(VendorDocument.DocumentType type, int pageNumber, int pageSize, String sortBy, String sortDirection);

    VendorDocumentDTO uploadDocument(
            String contractId,
            MultipartFile file,
            VendorDocument.DocumentType documentType,
            String uploadedBy,
            String description
    ) throws IOException;

    VendorDocumentDTO processApproval(Long id, ApprovalRequest request);

    void deleteDocument(Long id);
}
