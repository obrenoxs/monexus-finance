package com.monexus.finance.transaction.dto.response;

import com.monexus.finance.transaction.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record TransactionResponse(
        Long id,
        String description,
        BigDecimal amount,
        LocalDate date,
        String observation,
        TransactionType type,
        Long categoryId,
        String categoryName,
        LocalDateTime createdAt
) {}
