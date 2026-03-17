package com.buildsmart.vendor.repository;
import com.buildsmart.vendor.model.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    List<Delivery> findByContractContractId(Long contractId);
    List<Delivery> findByStatus(Delivery.DeliveryStatus status);
}
