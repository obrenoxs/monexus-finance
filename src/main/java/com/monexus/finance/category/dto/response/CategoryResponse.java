package com.monexus.finance.category.dto.response;

import com.monexus.finance.category.enums.CategoryType;

import java.time.LocalDateTime;

public record CategoryResponse(

        Long id,
        String name,
        CategoryType type,
        LocalDateTime createdAt
) {}
