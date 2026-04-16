package com.buildsmart.vendor.repository;
import com.buildsmart.vendor.model.Invoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, String> {
    List<Invoice> findByContractContractId(String contractId);
    List<Invoice> findByStatus(Invoice.InvoiceStatus status);
    Page<Invoice> findByContractContractId(String contractId, Pageable pageable);
    Page<Invoice> findByStatus(Invoice.InvoiceStatus status, Pageable pageable);
    boolean existsByInvoiceNumber(String invoiceNumber);
    Optional<Invoice> findTopByOrderByInvoiceIdDesc();
    boolean existsByInvoiceNumberAndInvoiceIdNot(String invoiceNumber, String invoiceId);
    boolean existsByContractContractIdAndDateAndAmount(
            String contractId,
            java.time.LocalDate date,
            java.math.BigDecimal amount
    );
    boolean existsByContractContractIdAndDateAndAmountAndInvoiceIdNot(
            String contractId,
            java.time.LocalDate date,
            java.math.BigDecimal amount,
            String invoiceId
    );
}
