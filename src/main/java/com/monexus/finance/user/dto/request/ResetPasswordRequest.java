package com.monexus.finance.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequest(

        @NotBlank(message = "Token é obrigatório")
        String token,

        @NotBlank(message = "Nova senha é obrigatória")
        @Size(min = 8, max = 100, message = "Senha deve ter entre 8 e 100 caracteres")
        String newPassword
) {}
