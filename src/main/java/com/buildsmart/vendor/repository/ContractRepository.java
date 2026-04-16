package com.buildsmart.vendor.repository;
import com.buildsmart.vendor.model.Contract;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
@Repository
public interface ContractRepository extends JpaRepository<Contract, String> {
    List<Contract> findByVendorVendorId(String vendorId);
    List<Contract> findByProjectProjectId(String projectId);
    List<Contract> findByStatus(Contract.ContractStatus status);
    Page<Contract> findByVendorVendorId(String vendorId, Pageable pageable);
    Page<Contract> findByProjectProjectId(String projectId, Pageable pageable);
    Page<Contract> findByStatus(Contract.ContractStatus status, Pageable pageable);
    Optional<Contract> findTopByOrderByContractIdDesc();
    boolean existsByVendorVendorIdAndProjectProjectIdAndStartDateAndEndDate(
            String vendorId,
            String projectId,
            java.time.LocalDate startDate,
            java.time.LocalDate endDate
    );
    boolean existsByVendorVendorIdAndProjectProjectIdAndStartDateAndEndDateAndContractIdNot(
            String vendorId,
            String projectId,
            java.time.LocalDate startDate,
            java.time.LocalDate endDate,
            String contractId
    );
}
