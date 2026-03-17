package com.buildsmart.vendor.repository;
import com.buildsmart.vendor.model.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface VendorRepository extends JpaRepository<Vendor, Long> {
    List<Vendor> findByStatus(Vendor.VendorStatus status);
    List<Vendor> findByNameContainingIgnoreCase(String name);
}
