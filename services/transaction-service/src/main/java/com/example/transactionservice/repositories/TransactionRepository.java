package com.example.transactionservice.repositories;

import com.example.transactionservice.entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction , UUID> {
    List<Transaction> findByFromAccountIdOrToAccountIdOrderByInitiatedAtDesc(
            UUID fromAccountId, UUID toAccountId);
}
