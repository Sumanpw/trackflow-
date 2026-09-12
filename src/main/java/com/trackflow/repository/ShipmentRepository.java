package com.trackflow.repository;

import com.trackflow.entity.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, Long> {

    Optional<Shipment> findByTrackingId(String trackingId);

    List<Shipment> findByCustomerId(Long customerId);

    List<Shipment> findByStatus(String status);

    List<Shipment> findByCustomerIdAndStatus(Long customerId, String status);

    boolean existsByTrackingId(String trackingId);
}