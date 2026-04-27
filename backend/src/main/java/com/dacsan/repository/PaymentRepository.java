package com.dacsan.repository;

import com.dacsan.entity.Payment;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findTopByOrderIdOrderByIdDesc(Long orderId);
    Optional<Payment> findByTransactionRef(String transactionRef);
}