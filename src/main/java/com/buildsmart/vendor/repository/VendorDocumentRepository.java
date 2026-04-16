package com.buildsmart.vendor.repository;
import com.buildsmart.vendor.model.VendorDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface VendorDocumentRepository extends JpaRepository<VendorDocument, Long> {
    List<VendorDocument> findByContractContractId(String contractId);
    List<VendorDocument> findByStatus(VendorDocument.DocumentStatus status);
    List<VendorDocument> findByDocumentType(VendorDocument.DocumentType documentType);
    Page<VendorDocument> findByContractContractId(String contractId, Pageable pageable);
    Page<VendorDocument> findByStatus(VendorDocument.DocumentStatus status, Pageable pageable);
    Page<VendorDocument> findByDocumentType(VendorDocument.DocumentType documentType, Pageable pageable);
    boolean existsByContractContractIdAndDocumentNameIgnoreCaseAndDocumentType(
            String contractId,
            String documentName,
            VendorDocument.DocumentType documentType
    );
}
