package com.monexus.finance.transaction.repository;

import com.monexus.finance.transaction.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findAllByWalletId(Long walletId);

    Optional<Transaction> findByIdAndWalletId(Long id, Long walletId);

    boolean existsByCategoryId(Long categoryId);

    @Query("""
        SELECT FUNCTION('DATE_FORMAT', t.date, '%Y-%m') as yearMonth,
             t.type as type,
             SUM(t.amount) as total
        FROM Transaction t
        WHERE t.wallet.id = :walletId
            AND (:startDate IS NULL OR t.date >= :startDate)
        GROUP BY FUNCTION('DATE_FORMAT', t.date, '%Y-%m'), t.type
        ORDER BY yearMonth ASC
        """)
    List<MonthlyAggregateProjection> findMonthlyAggregates(Long walletId, LocalDate startDate);
}
