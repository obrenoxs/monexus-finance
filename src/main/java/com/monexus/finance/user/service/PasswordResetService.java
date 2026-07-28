package com.monexus.finance.user.service;

import com.monexus.finance.user.entity.User;
import com.monexus.finance.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
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
    private final JavaMailSender mailSender;
    private final PasswordEncoder passwordEncoder;
    private final String mailFrom;
    private final String frontendUrl;

    public PasswordResetService(UserRepository userRepository,
                                JavaMailSender mailSender,
                                PasswordEncoder passwordEncoder,
                                @Value("${app.mail.from}") String mailFrom,
                                @Value("${app.frontend.url}") String frontendUrl) {
        this.userRepository = userRepository;
        this.mailSender = mailSender;
        this.passwordEncoder = passwordEncoder;
        this.mailFrom = mailFrom;
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

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(mailFrom);
        message.setTo(user.getEmail());
        message.setSubject("Redefinição de senha - Monexus Finance");
        message.setText(
                "Olá, " + user.getFirstName() + "!\n\n" +
                "Você solicitou a redefinição da sua senha. Clique no link abaixo:\n" +
                resetLink + "\n\n" +
                "Este link expira em 1 hora. Se você não solicitou isso, ignore este e-mail."
        );

        mailSender.send(message);
    }
}
