package com.monexus.finance.user.service;

import com.monexus.finance.shared.mail.BrevoEmailService;
import com.monexus.finance.user.entity.User;
import com.monexus.finance.user.exception.EmailAlreadyExistsException;
import com.monexus.finance.user.exception.InvalidEmailChangeTokenException;
import com.monexus.finance.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailChangeServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private BrevoEmailService brevoEmailService;

    private EmailChangeService emailChangeService;

    @BeforeEach
    void setUp() {
        emailChangeService = new EmailChangeService(
                userRepository, passwordEncoder, brevoEmailService, "http://localhost:8080");
    }

    @Test
    void shouldRequestEmailChangeAndSendEmailToNewAddress() {
        User user = User.builder().id(1L).firstName("Breno").email("old@example.com").password("hashed").build();

        when(passwordEncoder.matches("12345678", "hashed")).thenReturn(true);
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(userRepository.save(user)).thenReturn(user);

        emailChangeService.requestEmailChange(user, "new@example.com", "12345678");

        assertThat(user.getPendingEmail()).isEqualTo("new@example.com");
        assertThat(user.getEmailChangeToken()).isNotBlank();
        assertThat(user.getEmailChangeTokenExpiresAt()).isAfter(LocalDateTime.now().plusMinutes(50));

        verify(brevoEmailService).send(eq("new@example.com"), anyString(), anyString());
    }

    @Test
    void shouldThrowWhenCurrentPasswordIsIncorrect() {
        User user = User.builder().id(1L).email("old@example.com").password("hashed").build();

        when(passwordEncoder.matches("wrong-password", "hashed")).thenReturn(false);

        assertThatThrownBy(() -> emailChangeService.requestEmailChange(user, "new@example.com", "wrong-password"))
                .isInstanceOf(BadCredentialsException.class);

        verify(userRepository, never()).save(any());
        verify(brevoEmailService, never()).send(anyString(), anyString(), anyString());
    }

    @Test
    void shouldThrowWhenNewEmailAlreadyExists() {
        User user = User.builder().id(1L).email("old@example.com").password("hashed").build();

        when(passwordEncoder.matches("12345678", "hashed")).thenReturn(true);
        when(userRepository.existsByEmail("new@example.com")).thenReturn(true);

        assertThatThrownBy(() -> emailChangeService.requestEmailChange(user, "new@example.com", "12345678"))
                .isInstanceOf(EmailAlreadyExistsException.class);

        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldConfirmEmailChangeWhenTokenIsValid() {
        User user = User.builder()
                .id(1L).email("old@example.com").pendingEmail("new@example.com")
                .emailChangeToken("valid-token")
                .emailChangeTokenExpiresAt(LocalDateTime.now().plusMinutes(30))
                .build();

        when(userRepository.findByEmailChangeToken("valid-token")).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        emailChangeService.confirmEmailChange("valid-token");

        assertThat(user.getEmail()).isEqualTo("new@example.com");
        assertThat(user.getPendingEmail()).isNull();
        assertThat(user.getEmailChangeToken()).isNull();
        assertThat(user.getEmailChangeTokenExpiresAt()).isNull();
    }

    @Test
    void shouldThrowWhenEmailChangeTokenNotFound() {
        when(userRepository.findByEmailChangeToken("invalid-token")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> emailChangeService.confirmEmailChange("invalid-token"))
                .isInstanceOf(InvalidEmailChangeTokenException.class);
    }

    @Test
    void shouldThrowWhenEmailChangeTokenIsExpired() {
        User user = User.builder()
                .id(1L).email("old@example.com").pendingEmail("new@example.com")
                .emailChangeToken("expired-token")
                .emailChangeTokenExpiresAt(LocalDateTime.now().minusMinutes(5))
                .build();

        when(userRepository.findByEmailChangeToken("expired-token")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> emailChangeService.confirmEmailChange("expired-token"))
                .isInstanceOf(InvalidEmailChangeTokenException.class);

        verify(userRepository, never()).save(any());
    }
}