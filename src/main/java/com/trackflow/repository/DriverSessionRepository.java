package com.trackflow.repository;

import com.trackflow.entity.DriverSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DriverSessionRepository extends JpaRepository<DriverSession, Long> {

    Optional<DriverSession> findBySessionToken(String sessionToken);

    List<DriverSession> findByPartnerIdAndActive(Long partnerId, Boolean active);

    List<DriverSession> findByPartnerId(Long partnerId);
}