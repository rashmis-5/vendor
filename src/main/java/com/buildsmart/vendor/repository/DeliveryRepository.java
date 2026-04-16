package com.buildsmart.vendor.repository;
import com.buildsmart.vendor.model.Delivery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, String> {
    List<Delivery> findByContractContractId(String contractId);
    List<Delivery> findByStatus(Delivery.DeliveryStatus status);
    Page<Delivery> findByContractContractId(String contractId, Pageable pageable);
    Page<Delivery> findByStatus(Delivery.DeliveryStatus status, Pageable pageable);
    Optional<Delivery> findTopByOrderByDeliveryIdDesc();
    boolean existsByContractContractIdAndDateAndItemIgnoreCase(
            String contractId,
            java.time.LocalDate date,
            String item
    );
    boolean existsByContractContractIdAndDateAndItemIgnoreCaseAndDeliveryIdNot(
            String contractId,
            java.time.LocalDate date,
            String item,
            String deliveryId
    );
}
