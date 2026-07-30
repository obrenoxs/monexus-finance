package com.monexus.finance.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ChangeEmailRequest(

        @NotBlank(message = "Novo e-mail é obrigatório")
        @Email(message = "E-mail inválido")
        String newEmail,

        @NotBlank(message = "Senha atual é obrigatória")
        String currentPassword
) {}
