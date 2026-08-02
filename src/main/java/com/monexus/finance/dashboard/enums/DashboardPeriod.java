package com.monexus.finance.dashboard.enums;

import java.time.LocalDate;
import java.time.YearMonth;

public enum DashboardPeriod {
    CURRENT_MONTH,
    LAST_3_MONTHS,
    LAST_12_MONTHS,
    ALL_TIME;

    public LocalDate resolveStartDate() {
        YearMonth current = YearMonth.now();
        return switch (this) {
            case CURRENT_MONTH -> current.atDay(1);
            case LAST_3_MONTHS -> current.minusMonths(2).atDay(1);
            case LAST_12_MONTHS -> current.minusMonths(11).atDay(1);
            case ALL_TIME -> null;
        };
    }
}
