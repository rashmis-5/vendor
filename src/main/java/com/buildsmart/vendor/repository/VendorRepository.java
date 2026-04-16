package com.buildsmart.vendor.repository;
import com.buildsmart.vendor.model.Vendor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
@Repository
public interface VendorRepository extends JpaRepository<Vendor, String> {
    List<Vendor> findByStatus(Vendor.VendorStatus status);
    List<Vendor> findByNameContainingIgnoreCase(String name);
    Page<Vendor> findByStatus(Vendor.VendorStatus status, Pageable pageable);
    Page<Vendor> findByNameContainingIgnoreCase(String name, Pageable pageable);
    Optional<Vendor> findTopByOrderByVendorIdDesc();
    Optional<Vendor> findByEmail(String email);
    Optional<Vendor> findByNameIgnoreCase(String name);
    Optional<Vendor> findByEmailIgnoreCase(String email);
    Optional<Vendor> findByPhone(String phone);
}
