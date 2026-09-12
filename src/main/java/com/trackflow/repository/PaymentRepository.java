package com.trackflow.repository;

import com.trackflow.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByPaymentId(String paymentId);
    Optional<Payment> findByIdempotencyKey(String idempotencyKey);
    List<Payment> findByCustomerId(Long customerId);
    List<Payment> findByShipmentId(Long shipmentId);
    List<Payment> findByPaymentStatus(String paymentStatus);
    boolean existsByIdempotencyKey(String idempotencyKey);
}