package com.monexus.finance.transaction.dto.response;

import java.math.BigDecimal;
import java.time.YearMonth;

public record MonthlySummary(
        YearMonth yearMonth,
        BigDecimal income,
        BigDecimal expense,
        BigDecimal balance
) {}
