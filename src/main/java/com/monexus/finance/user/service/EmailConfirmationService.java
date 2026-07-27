package com.monexus.finance.user.service;

import com.monexus.finance.user.entity.User;
import com.monexus.finance.user.exception.InvalidConfirmationTokenException;
import com.monexus.finance.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class EmailConfirmationService {

    private static final long TOKEN_EXPIRATION_HOURS = 24;

    private final UserRepository userRepository;
    private final JavaMailSender mailSender;
    private final String mailFrom;
    private final String frontendUrl;

    public EmailConfirmationService(UserRepository userRepository,
                                    JavaMailSender mailSender,
                                    @Value("${app.mail.from}") String mailFrom,
                                    @Value("${app.frontend.url}") String frontendUrl) {
        this.userRepository = userRepository;
        this.mailSender = mailSender;
        this.mailFrom = mailFrom;
        this.frontendUrl = frontendUrl;
    }

    @Transactional
    public void sendConfirmationEmail(User user) {
        String token = UUID.randomUUID().toString();

        user.setConfirmationToken(token);
        user.setConfirmationTokenExpiresAt(LocalDateTime.now().plusHours(TOKEN_EXPIRATION_HOURS));

        userRepository.save(user);

        String confirmationLink = frontendUrl + "/api/v1/auth/confirm-email?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(mailFrom);
        message.setTo(user.getEmail());
        message.setSubject("Confirme seu e-mail - Monexus Finance");
        message.setText(
                "Olá, " + user.getFirstName() + "!\n\n" +
                "Confirme seu e-mail clicando no link abaixo:\n" +
                confirmationLink + "\n\n" +
                "Este link expira em 24 horas."
        );

        mailSender.send(message);
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
