package com.buildsmart.vendor.repository;
import com.buildsmart.vendor.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    List<Invoice> findByContractContractId(Long contractId);
    List<Invoice> findByStatus(Invoice.InvoiceStatus status);
    boolean existsByInvoiceNumber(String invoiceNumber);
}
