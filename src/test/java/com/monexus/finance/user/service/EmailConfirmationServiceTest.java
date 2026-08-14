package com.monexus.finance.user.service;

import com.monexus.finance.shared.mail.BrevoEmailService;
import com.monexus.finance.user.entity.User;
import com.monexus.finance.user.exception.InvalidConfirmationTokenException;
import com.monexus.finance.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailConfirmationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BrevoEmailService brevoEmailService;

    private EmailConfirmationService emailConfirmationService;

    @BeforeEach
    void setUp() {
        emailConfirmationService = new EmailConfirmationService(
                userRepository, brevoEmailService, "http://localhost:8080");
    }

    @Test
    void shouldGenerateTokenAndSendConfirmationEmail() {
        User user = User.builder().id(1L).email("breno@example.com").firstName("Breno").build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        emailConfirmationService.sendConfirmationEmail(user);

        assertThat(user.getConfirmationToken()).isNotBlank();
        assertThat(user.getConfirmationTokenExpiresAt()).isAfter(LocalDateTime.now().plusHours(23));

        verify(brevoEmailService).send(eq("breno@example.com"), anyString(), anyString());
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