package com.buildsmart.vendor.service;

import com.buildsmart.vendor.dto.ApprovalRequest;
import com.buildsmart.vendor.dto.InvoiceDTO;
import com.buildsmart.vendor.dto.PageableResponse;
import com.buildsmart.vendor.model.Invoice;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface InvoiceService {

    List<InvoiceDTO> getAllInvoices();

    PageableResponse<InvoiceDTO> getAllInvoices(int pageNumber, int pageSize, String sortBy, String sortDirection);

    InvoiceDTO getInvoiceById(String id);

    List<InvoiceDTO> getInvoicesByContract(String contractId);

    PageableResponse<InvoiceDTO> getInvoicesByContract(String contractId, int pageNumber, int pageSize, String sortBy, String sortDirection);

    List<InvoiceDTO> getInvoicesByStatus(Invoice.InvoiceStatus status);

    PageableResponse<InvoiceDTO> getInvoicesByStatus(Invoice.InvoiceStatus status, int pageNumber, int pageSize, String sortBy, String sortDirection);

    InvoiceDTO createInvoice(InvoiceDTO dto);

    InvoiceDTO updateInvoice(String id, InvoiceDTO dto);

    InvoiceDTO uploadInvoiceFile(String id, MultipartFile file, String submittedBy) throws IOException;

    InvoiceDTO processApproval(String id, ApprovalRequest request);

    InvoiceDTO markAsPaid(String id);

    InvoiceDTO updateInvoiceStatus(String id, Invoice.InvoiceStatus status);

    void deleteInvoice(String id);
}