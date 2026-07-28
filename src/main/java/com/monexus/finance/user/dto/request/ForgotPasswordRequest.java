package com.monexus.finance.user.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ForgotPasswordRequest(

        @NotBlank(message = "E-mail é obrigatório")
        String email
) {}
