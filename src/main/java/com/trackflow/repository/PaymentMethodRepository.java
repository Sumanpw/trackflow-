package com.trackflow.repository;

import com.trackflow.entity.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Long> {
    List<PaymentMethod> findByUserId(Long userId);
    List<PaymentMethod> findByUserIdAndActive(Long userId, Boolean active);
    Optional<PaymentMethod> findByUserIdAndDefaultMethod(Long userId, Boolean defaultMethod);
}