package com.trackflow.repository;

import com.trackflow.entity.DeliveryReceipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeliveryReceiptRepository extends JpaRepository<DeliveryReceipt, Long> {

    Optional<DeliveryReceipt> findByReceiptNumber(String receiptNumber);

    Optional<DeliveryReceipt> findByDeliveryId(Long deliveryId);

    boolean existsByReceiptNumber(String receiptNumber);
}