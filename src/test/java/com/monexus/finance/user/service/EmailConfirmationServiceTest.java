package com.monexus.finance.user.service;

import com.monexus.finance.user.entity.User;
import com.monexus.finance.user.exception.InvalidConfirmationTokenException;
import com.monexus.finance.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailConfirmationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JavaMailSender mailSender;

    private EmailConfirmationService emailConfirmationService;

    @BeforeEach
    void setUp() {
        emailConfirmationService = new EmailConfirmationService(
                userRepository, mailSender, "no-reply@monexusfinance.com", "http://localhost:8080");
    }

    @Test
    void shouldGenerateTokenAndSendConfirmationEmail() {
        User user = User.builder().id(1L).email("breno@example.com").firstName("Breno").build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        emailConfirmationService.sendConfirmationEmail(user);

        assertThat(user.getConfirmationToken()).isNotBlank();
        assertThat(user.getConfirmationTokenExpiresAt()).isAfter(LocalDateTime.now().plusHours(23));

        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(messageCaptor.capture());

        SimpleMailMessage sentMessage = messageCaptor.getValue();
        assertThat(sentMessage.getTo()).containsExactly("breno@example.com");
        assertThat(sentMessage.getFrom()).isEqualTo("no-reply@monexusfinance.com");
    }

    @Test
    void shouldConfirmEmailWhenTokenIsValid() {
        User user = User.builder()
                .id(1L).email("breno@example.com")
                .confirmationToken("valid-token")
                .confirmationTokenExpiresAt(LocalDateTime.now().plusHours(1))
                .build();

        when(userRepository.findByConfirmationToken("valid-token")).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        emailConfirmationService.confirmEmail("valid-token");

        assertThat(user.isEmailVerified()).isTrue();
        assertThat(user.getConfirmationToken()).isNull();
        assertThat(user.getConfirmationTokenExpiresAt()).isNull();
    }

    @Test
    void shouldThrowWhenTokenNotFound() {
        when(userRepository.findByConfirmationToken("invalid-token")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> emailConfirmationService.confirmEmail("invalid-token"))
                .isInstanceOf(InvalidConfirmationTokenException.class);
    }

    @Test
    void shouldThrowWhenTokenIsExpired() {
        User user = User.builder()
                .id(1L).email("breno@example.com")
                .confirmationToken("expired-token")
                .confirmationTokenExpiresAt(LocalDateTime.now().minusHours(1))
                .build();

        when(userRepository.findByConfirmationToken("expired-token")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> emailConfirmationService.confirmEmail("expired-token"))
                .isInstanceOf(InvalidConfirmationTokenException.class);

        verify(userRepository, never()).save(any());
    }
}
