package com.trackflow.repository;

import com.trackflow.entity.DeliveryPartner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryPartnerRepository extends JpaRepository<DeliveryPartner, Long> {

    Optional<DeliveryPartner> findByPartnerCode(String partnerCode);

    Optional<DeliveryPartner> findByUserId(Long userId);

    List<DeliveryPartner> findByAvailable(Boolean available);

    List<DeliveryPartner> findByAvailableAndVerified(Boolean available, Boolean verified);

    boolean existsByPartnerCode(String partnerCode);
}