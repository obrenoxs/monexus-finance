package com.monexus.finance.user.dto.response;

import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        String profileImage,
        boolean emailVerified,
        LocalDateTime createdAt
) {}
