package com.buildsmart.vendor.controller;
import com.buildsmart.vendor.dto.*;
import com.buildsmart.vendor.model.VendorDocument;
import com.buildsmart.vendor.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.List;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
@Tag(name = "Vendor Document Management", description = "APIs for uploading, retrieving, approving/rejecting, and downloading vendor compliance documents")
public class VendorDocumentController {
    private static final Logger log = LoggerFactory.getLogger(VendorDocumentController.class);
    private final VendorDocumentService documentService;
    private final FileStorageService fileStorageService;

    @Operation(
        summary = "Get all documents",
        description = "Returns a paginated or full list of vendor documents. Supports filtering by contractId, status, or documentType."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Documents retrieved successfully")
    })
    @GetMapping
    public ResponseEntity<?> getAll(
            @Parameter(description = "Filter by contract ID") @RequestParam(required = false) String contractId,
            @Parameter(description = "Filter by document status") @RequestParam(required = false) VendorDocument.DocumentStatus status,
            @Parameter(description = "Filter by document type") @RequestParam(required = false) VendorDocument.DocumentType documentType,
            @Parameter(description = "Page number (0-based)") @RequestParam(required = false, defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(required = false, defaultValue = "10") int size,
            @Parameter(description = "Field to sort by") @RequestParam(required = false, defaultValue = "documentId") String sortBy,
            @Parameter(description = "Sort direction: asc or desc") @RequestParam(required = false, defaultValue = "asc") String sortDirection) {

        if (page >= 0 && size > 0) {
            if (contractId != null) {
                PageableResponse<VendorDocumentDTO> result = documentService.getDocumentsByContract(contractId, page, size, sortBy, sortDirection);
                return ResponseEntity.ok(ApiResponse.success(result, "Documents retrieved"));
            } else if (status != null) {
                PageableResponse<VendorDocumentDTO> result = documentService.getDocumentsByStatus(status, page, size, sortBy, sortDirection);
                return ResponseEntity.ok(ApiResponse.success(result, "Documents retrieved"));
            } else if (documentType != null) {
                PageableResponse<VendorDocumentDTO> result = documentService.getDocumentsByType(documentType, page, size, sortBy, sortDirection);
                return ResponseEntity.ok(ApiResponse.success(result, "Documents retrieved"));
            } else {
                PageableResponse<VendorDocumentDTO> result = documentService.getAllDocuments(page, size, sortBy, sortDirection);
                return ResponseEntity.ok(ApiResponse.success(result, "Documents retrieved"));
            }
        } else {
            List<VendorDocumentDTO> result;
            if (contractId != null) result = documentService.getDocumentsByContract(contractId);
            else if (status != null) result = documentService.getDocumentsByStatus(status);
            else if (documentType != null) result = documentService.getDocumentsByType(documentType);
            else result = documentService.getAllDocuments();
            return ResponseEntity.ok(ApiResponse.success(result, "Documents retrieved"));
        }
    }

    @Operation(summary = "Get document by ID", description = "Retrieves metadata for a single document by its unique ID")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Document retrieved"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Document not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<VendorDocumentDTO>> getById(
            @Parameter(description = "Document ID", required = true) @PathVariable Long id) {
        log.info("Fetching document by id={}", id);
        return ResponseEntity.ok(ApiResponse.success(documentService.getDocumentById(id), "Document retrieved"));
    }

    @Operation(summary = "Upload a vendor document", description = "Uploads a document file and associates it with a contract. The document is submitted for approval automatically.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Document uploaded and submitted for approval"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    })
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<VendorDocumentDTO>> uploadDocument(
            @Parameter(description = "Contract ID to associate the document with", required = true) @RequestParam String contractId,
            @Parameter(description = "Document file to upload", required = true) @RequestParam("file") MultipartFile file,
            @Parameter(description = "Type/category of the document", required = true) @RequestParam VendorDocument.DocumentType documentType,
            @Parameter(description = "Name of the uploader") @RequestParam(defaultValue = "vendor") String uploadedBy,
            @Parameter(description = "Optional description of the document") @RequestParam(required = false) String description) throws IOException {
        log.info("Uploading document for contractId={}, type={}, uploadedBy={}", contractId, documentType, uploadedBy);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(
                documentService.uploadDocument(contractId, file, documentType, uploadedBy, description),
                "Document uploaded and submitted for approval"));
    }

    @Operation(summary = "Approve or reject a document", description = "Processes the approval or rejection of a submitted vendor document")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Document approval processed"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Document not found", content = @Content)
    })
    @PostMapping("/{id}/approval")
    public ResponseEntity<ApiResponse<VendorDocumentDTO>> processApproval(
            @Parameter(description = "Document ID", required = true) @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Approval or rejection details", required = true)
            @RequestBody ApprovalRequest request) {
        log.info("Processing approval for document id={}, approved={}, by={}", id, request.isApproved(), request.getApprovedBy());
        return ResponseEntity.ok(ApiResponse.success(documentService.processApproval(id, request),
                request.isApproved() ? "Document approved" : "Document rejected"));
    }

    @Operation(summary = "Download a document", description = "Downloads the actual file of a vendor document by its ID")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "File downloaded successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Document or file not found", content = @Content)
    })
    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> downloadDocument(
            @Parameter(description = "Document ID", required = true) @PathVariable Long id) throws MalformedURLException {
        log.info("Downloading document id={}", id);
        VendorDocumentDTO doc = documentService.getDocumentById(id);
        Path filePath = fileStorageService.getFilePath(doc.getFilePath());
        Resource resource = new UrlResource(filePath.toUri());
        if (!resource.exists()) return ResponseEntity.notFound().build();
        String contentType = doc.getFileType() != null ? doc.getFileType() : "application/octet-stream";
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + doc.getDocumentName() + "\"")
                .body(resource);
    }

    @Operation(summary = "Delete a document", description = "Deletes a vendor document record by ID")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Document deleted"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Document not found", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @Parameter(description = "Document ID", required = true) @PathVariable Long id) {
        log.info("Deleting document id={}", id);
        documentService.deleteDocument(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Document deleted"));
    }
}
