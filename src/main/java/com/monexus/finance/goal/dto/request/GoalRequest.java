package com.monexus.finance.goal.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record GoalRequest(

        @NotBlank(message = "Nome é obrigatório")
        @Size(max = 150)
        String title,

        @NotNull(message = "Valor alvo é obrigatório")
        @Positive(message = "Valor alvo deve ser maior que zero")
        BigDecimal targetAmount,

        LocalDate targetDate
) {}
