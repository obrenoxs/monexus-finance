package com.monexus.finance.user.service;

import com.monexus.finance.shared.mail.BrevoEmailService;
import com.monexus.finance.user.entity.User;
import com.monexus.finance.user.exception.InvalidConfirmationTokenException;
import com.monexus.finance.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class EmailConfirmationService {

    private static final long TOKEN_EXPIRATION_HOURS = 24;

    private final UserRepository userRepository;
    private final BrevoEmailService brevoEmailService;
    private final String backendUrl;

    public EmailConfirmationService(UserRepository userRepository,
                                    BrevoEmailService brevoEmailService,
                                    @Value("${app.backend.url}") String backendUrl) {
        this.userRepository = userRepository;
        this.brevoEmailService = brevoEmailService;
        this.backendUrl = backendUrl;
    }

    @Transactional
    public void sendConfirmationEmail(User user) {
        User managedUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new IllegalStateException("Usuário não encontrado ao gerar token de confirmação: " + user.getId()));

        String token = UUID.randomUUID().toString();

        managedUser.setConfirmationToken(token);
        managedUser.setConfirmationTokenExpiresAt(LocalDateTime.now().plusHours(TOKEN_EXPIRATION_HOURS));

        userRepository.save(managedUser);

        String confirmationLink = backendUrl + "/api/v1/auth/confirm-email?token=" + token;

        String body = "Olá, " + managedUser.getFirstName() + "!\n\n" +
                "Confirme seu e-mail clicando no link abaixo:\n" +
                confirmationLink + "\n\n" +
                "Este link expira em 24 horas.";

        brevoEmailService.send(managedUser.getEmail(), "Confirme seu e-mail - Monexus Finance", body);
    }

    @Transactional
    public void confirmEmail(String token) {
        User user = userRepository.findByConfirmationToken(token)
                .orElseThrow(() -> new InvalidConfirmationTokenException("Token de confirmação inválido."));

        if (LocalDateTime.now().isAfter(user.getConfirmationTokenExpiresAt())) {
            throw new InvalidConfirmationTokenException("Este link de confirmação expirou. Solicite um novo.");
        }

        user.setEmailVerified(true);
        user.setConfirmationToken(null);
        user.setConfirmationTokenExpiresAt(null);

        userRepository.save(user);
    }
}