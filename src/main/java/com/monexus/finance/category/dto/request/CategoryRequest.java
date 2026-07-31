package com.monexus.finance.category.dto.request;

import com.monexus.finance.category.enums.CategoryType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CategoryRequest(

        @NotBlank(message = "Nome é obrigatório")
        @Size(max = 100)
        String name,

        @NotNull
        CategoryType type
) {}
