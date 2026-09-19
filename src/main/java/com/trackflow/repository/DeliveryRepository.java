package com.trackflow.repository;

import com.trackflow.entity.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {

    Optional<Delivery> findByShipmentId(Long shipmentId);

    List<Delivery> findByPartnerId(Long partnerId);

    List<Delivery> findByAssignmentStatus(String assignmentStatus);

    List<Delivery> findByPartnerIdAndAssignmentStatus(Long partnerId, String status);
}