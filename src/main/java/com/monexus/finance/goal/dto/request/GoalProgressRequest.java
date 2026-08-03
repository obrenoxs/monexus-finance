package com.monexus.finance.goal.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record GoalProgressRequest(

        @NotNull(message = "Valor é obrigatório")
        @PositiveOrZero(message = "Valor não pode ser negativo")
        BigDecimal currentAmount
) {}
