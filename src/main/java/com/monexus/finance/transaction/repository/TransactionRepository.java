package com.monexus.finance.transaction.repository;

import com.monexus.finance.transaction.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findAllByWalletId(Long walletId);

    Optional<Transaction> findByIdAndWalletId(Long id, Long walletId);

    boolean existsByCategoryId(Long categoryId);
}
