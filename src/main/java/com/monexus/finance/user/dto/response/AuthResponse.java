package com.monexus.finance.user.dto.response;

public record AuthResponse(
        String token,
        String tokenType
) {
    public AuthResponse(String token) {
        this(token, "Bearer");
    }
}
