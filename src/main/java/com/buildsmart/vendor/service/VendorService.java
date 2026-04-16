package com.buildsmart.vendor.service;

import com.buildsmart.vendor.dto.PageableResponse;
import com.buildsmart.vendor.dto.VendorDTO;
import com.buildsmart.vendor.model.Vendor;

import java.util.List;

public interface VendorService {

    List<VendorDTO> getAllVendors();

    PageableResponse<VendorDTO> getAllVendors(int pageNumber, int pageSize, String sortBy, String sortDirection);

    VendorDTO getVendorById(String id);

    List<VendorDTO> getVendorsByStatus(Vendor.VendorStatus status);

    PageableResponse<VendorDTO> getVendorsByStatus(Vendor.VendorStatus status, int pageNumber, int pageSize, String sortBy, String sortDirection);

    List<VendorDTO> searchVendorsByName(String name);

    PageableResponse<VendorDTO> searchVendorsByName(String name, int pageNumber, int pageSize, String sortBy, String sortDirection);

    VendorDTO createVendor(VendorDTO dto);

    VendorDTO updateVendor(String id, VendorDTO dto);

    VendorDTO updateVendorStatus(String id, Vendor.VendorStatus status);

    void deleteVendor(String id);
}