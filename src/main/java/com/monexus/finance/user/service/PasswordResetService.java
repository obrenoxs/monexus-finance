package com.monexus.finance.user.service;

import com.monexus.finance.shared.mail.BrevoEmailService;
import com.monexus.finance.user.entity.User;
import com.monexus.finance.user.exception.InvalidResetPasswordTokenException;
import com.monexus.finance.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordResetService {

    private static final long TOKEN_EXPIRATION_HOURS = 1;

    private final UserRepository userRepository;
    private final BrevoEmailService brevoEmailService;
    private final PasswordEncoder passwordEncoder;
    private final String frontendUrl;

    public PasswordResetService(UserRepository userRepository,
                                BrevoEmailService brevoEmailService,
                                PasswordEncoder passwordEncoder,
                                @Value("${app.frontend.url}") String frontendUrl) {
        this.userRepository = userRepository;
        this.brevoEmailService = brevoEmailService;
        this.passwordEncoder = passwordEncoder;
        this.frontendUrl = frontendUrl;
    }

    @Transactional
    public void requestPasswordReset(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            return;
        }

        User user = optionalUser.get();

        String token = UUID.randomUUID().toString();
        user.setResetPasswordToken(token);
        user.setResetPasswordTokenExpiresAt(LocalDateTime.now().plusHours(TOKEN_EXPIRATION_HOURS));

        userRepository.save(user);

        sendResetPasswordEmail(user, token);
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        User user = userRepository.findByResetPasswordToken(token)
                .orElseThrow(() -> new InvalidResetPasswordTokenException("Token de redefinição inválido."));

        if (LocalDateTime.now().isAfter(user.getResetPasswordTokenExpiresAt())) {
            throw new InvalidResetPasswordTokenException("Este link de redefinição expirou. Solicite um novo.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetPasswordToken(null);
        user.setResetPasswordTokenExpiresAt(null);

        userRepository.save(user);
    }

    private void sendResetPasswordEmail(User user, String token) {
        String resetLink = frontendUrl + "/reset-password?token=" + token;

        String body = "Olá, " + user.getFirstName() + "!\n\n" +
                "Você solicitou a redefinição da sua senha. Clique no link abaixo:\n" +
                resetLink + "\n\n" +
                "Este link expira em 1 hora. Se você não solicitou isso, ignore este e-mail.";

        brevoEmailService.send(user.getEmail(), "Redefinição de senha - Monexus Finance", body);
    }
}