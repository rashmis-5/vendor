package com.buildsmart.vendor.repository;
import com.buildsmart.vendor.model.VendorDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface VendorDocumentRepository extends JpaRepository<VendorDocument, Long> {
    List<VendorDocument> findByContractContractId(Long contractId);
    List<VendorDocument> findByStatus(VendorDocument.DocumentStatus status);
    List<VendorDocument> findByDocumentType(VendorDocument.DocumentType documentType);
}
