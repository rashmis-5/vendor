package com.buildsmart.vendor.repository;
import com.buildsmart.vendor.model.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface ContractRepository extends JpaRepository<Contract, Long> {
    List<Contract> findByVendorVendorId(Long vendorId);
    List<Contract> findByProjectId(String projectId);
    List<Contract> findByStatus(Contract.ContractStatus status);
}
