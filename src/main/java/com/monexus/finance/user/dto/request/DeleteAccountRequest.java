package com.monexus.finance.user.dto.request;

import jakarta.validation.constraints.NotBlank;

public record DeleteAccountRequest(

        @NotBlank(message = "Senha atual é obrigatória")
        String currentPassword
) {}
