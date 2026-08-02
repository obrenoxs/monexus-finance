package com.monexus.finance.transaction.repository;

import com.monexus.finance.transaction.enums.TransactionType;

import java.math.BigDecimal;

public interface MonthlyAggregateProjection {
    String getYearMonth();
    TransactionType getType();
    BigDecimal getTotal();
}
