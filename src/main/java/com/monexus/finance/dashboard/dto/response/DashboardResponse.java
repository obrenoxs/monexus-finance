package com.monexus.finance.dashboard.dto.response;

import com.monexus.finance.transaction.dto.response.MonthlySummary;

import java.math.BigDecimal;
import java.util.List;

public record DashboardResponse(
        BigDecimal currentBalance,
        BigDecimal monthlyIncome,
        BigDecimal monthlyExpense,
        BigDecimal monthlyBalance,
        List<MonthlySummary> history
) {}
