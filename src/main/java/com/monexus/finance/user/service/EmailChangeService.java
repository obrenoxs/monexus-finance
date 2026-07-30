package com.monexus.finance.user.service;

import com.monexus.finance.user.entity.User;
import com.monexus.finance.user.exception.EmailAlreadyExistsException;
import com.monexus.finance.user.exception.InvalidEmailChangeTokenException;
import com.monexus.finance.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class EmailChangeService {

    private static final long TOKEN_EXPIRATION_HOURS = 1;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;
    private final String mailFrom;
    private final String frontendUrl;

    public EmailChangeService(UserRepository userRepository,
                              PasswordEncoder passwordEncoder,
                              JavaMailSender mailSender,
                              @Value("${app.mail.from}") String mailFrom,
                              @Value("${app.frontend.url}") String frontendUrl) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailSender = mailSender;
        this.mailFrom = mailFrom;
        this.frontendUrl = frontendUrl;
    }

    @Transactional
    public void requestEmailChange(User authenticatedUser, String newEmail, String currentPassword) {

        if (!passwordEncoder.matches(currentPassword, authenticatedUser.getPassword())) {
            throw new BadCredentialsException("Senha atual incorreta.");
        }

        if (userRepository.existsByEmail(newEmail)) {
            throw new EmailAlreadyExistsException(newEmail);
        }

        String token = UUID.randomUUID().toString();

        authenticatedUser.setPendingEmail(newEmail);
        authenticatedUser.setEmailChangeToken(token);
        authenticatedUser.setEmailChangeTokenExpiresAt(LocalDateTime.now().plusHours(TOKEN_EXPIRATION_HOURS));

        userRepository.save(authenticatedUser);

        String confirmationLink = frontendUrl + "/api/v1/users/me/confirm-email-change?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(mailFrom);
        message.setTo(newEmail);
        message.setSubject("Confirme seu novo e-mail - Monexus Finance");
        message.setText(
            "Olá, " + authenticatedUser.getFirstName() + "!\n\n" +
            "Confirme seu novo e-mail clicando no link abaixo:\n" +
            confirmationLink + "\n\n" +
            "Este link expira em 1 hora. Se você não solicitou isso, ignore este e-mail."
        );

        mailSender.send(message);
    }

    @Transactional
    public void confirmEmailChange(String token) {
        User user = userRepository.findByEmailChangeToken(token)
                .orElseThrow(() -> new InvalidEmailChangeTokenException("Token de alteração de e-mail inválido."));

        if (LocalDateTime.now().isAfter(user.getEmailChangeTokenExpiresAt())) {
            throw new InvalidEmailChangeTokenException("Este link de alteração expirou. Solicite novamente.");
        }

        user.setEmail(user.getPendingEmail());
        user.setPendingEmail(null);
        user.setEmailChangeToken(null);
        user.setEmailChangeTokenExpiresAt(null);

        userRepository.save(user);
    }
}
