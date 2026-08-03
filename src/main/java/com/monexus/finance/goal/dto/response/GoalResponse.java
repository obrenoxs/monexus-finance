package com.monexus.finance.goal.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record GoalResponse(

        Long id,
        String title,
        BigDecimal targetAmount,
        BigDecimal currentAmount,
        LocalDate targetDate,
        BigDecimal remainingAmount,
        double progressPercentage,
        LocalDateTime createdAt
) {}
