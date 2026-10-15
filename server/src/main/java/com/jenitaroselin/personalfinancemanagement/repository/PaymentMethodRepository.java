package com.jenitaroselin.personalfinancemanagement.repository;

import com.jenitaroselin.personalfinancemanagement.entity.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Long> {
    List<PaymentMethod> findByUser_UserId(Long userId);
    Optional<PaymentMethod> findByPaymentMethodIdAndUser_UserId(Long paymentMethodId, Long userId);
    boolean existsByUser_UserIdAndNameIgnoreCase(Long userId, String name);
}
