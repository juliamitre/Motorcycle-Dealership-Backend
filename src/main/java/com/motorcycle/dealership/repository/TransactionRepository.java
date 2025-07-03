package com.motorcycle.dealership.repository;

import com.motorcycle.dealership.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Optional<Transaction> findByPaymentIntentId(String paymentIntentId);
}
