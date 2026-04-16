package com.buildsmart.vendor.service;

import com.buildsmart.vendor.dto.ContractDTO;
import com.buildsmart.vendor.dto.PageableResponse;
import com.buildsmart.vendor.model.Contract;

import java.util.List;

public interface ContractService {

    List<ContractDTO> getAllContracts();

    PageableResponse<ContractDTO> getAllContracts(int pageNumber, int pageSize, String sortBy, String sortDirection);

    ContractDTO getContractById(String id);

    List<ContractDTO> getContractsByVendor(String vendorId);

    PageableResponse<ContractDTO> getContractsByVendor(String vendorId, int pageNumber, int pageSize, String sortBy, String sortDirection);

    List<ContractDTO> getContractsByProject(String projectId);

    PageableResponse<ContractDTO> getContractsByProject(String projectId, int pageNumber, int pageSize, String sortBy, String sortDirection);

    List<ContractDTO> getContractsByStatus(Contract.ContractStatus status);

    PageableResponse<ContractDTO> getContractsByStatus(Contract.ContractStatus status, int pageNumber, int pageSize, String sortBy, String sortDirection);

    ContractDTO createContract(ContractDTO dto);

    ContractDTO updateContract(String id, ContractDTO dto);

    ContractDTO updateContractStatus(String id, Contract.ContractStatus status);

    void deleteContract(String id);
}
