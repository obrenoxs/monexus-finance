package com.monexus.finance.user.service;

import com.monexus.finance.shared.mail.BrevoEmailService;
import com.monexus.finance.user.entity.User;
import com.monexus.finance.user.exception.InvalidResetPasswordTokenException;
import com.monexus.finance.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasswordResetServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BrevoEmailService brevoEmailService;

    @Mock
    private PasswordEncoder passwordEncoder;

    private PasswordResetService passwordResetService;

    @BeforeEach
    void setUp() {
        passwordResetService = new PasswordResetService(
                userRepository, brevoEmailService, passwordEncoder, "http://localhost:8080");
    }

    @Test
    void shouldGenerateTokenAndSendEmailWhenUserExists() {
        User user = User.builder().id(1L).email("breno@example.com").firstName("Breno").build();

        when(userRepository.findByEmail("breno@example.com")).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        passwordResetService.requestPasswordReset("breno@example.com");

        assertThat(user.getResetPasswordToken()).isNotBlank();
        assertThat(user.getResetPasswordTokenExpiresAt()).isAfter(LocalDateTime.now().plusMinutes(50));

        verify(brevoEmailService).send(eq("breno@example.com"), anyString(), anyString());
    }

    @Test
    void shouldDoNothingWhenEmailDoesNotExist() {
        when(userRepository.findByEmail("naoexiste@example.com")).thenReturn(Optional.empty());

        passwordResetService.requestPasswordReset("naoexiste@example.com");

        verify(userRepository, never()).save(any());
        verify(brevoEmailService, never()).send(anyString(), anyString(), anyString());
    }

    @Test
    void shouldResetPasswordWhenTokenIsValid() {
        User user = User.builder()
                .id(1L).email("breno@example.com").password("old-hash")
                .resetPasswordToken("valid-token")
                .resetPasswordTokenExpiresAt(LocalDateTime.now().plusMinutes(30))
                .build();

        when(userRepository.findByResetPasswordToken("valid-token")).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newPassword123")).thenReturn("new-hash");
        when(userRepository.save(user)).thenReturn(user);

        passwordResetService.resetPassword("valid-token", "newPassword123");

        assertThat(user.getPassword()).isEqualTo("new-hash");
        assertThat(user.getResetPasswordToken()).isNull();
        assertThat(user.getResetPasswordTokenExpiresAt()).isNull();
    }

    @Test
    void shouldThrowWhenResetTokenNotFound() {
        when(userRepository.findByResetPasswordToken("invalid-token")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> passwordResetService.resetPassword("invalid-token", "newPassword123"))
                .isInstanceOf(InvalidResetPasswordTokenException.class);
    }

    @Test
    void shouldThrowWhenResetTokenIsExpired() {
        User user = User.builder()
                .id(1L).email("breno@example.com")
                .resetPasswordToken("expired-token")
                .resetPasswordTokenExpiresAt(LocalDateTime.now().minusMinutes(5))
                .build();

        when(userRepository.findByResetPasswordToken("expired-token")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> passwordResetService.resetPassword("expired-token", "newPassword123"))
                .isInstanceOf(InvalidResetPasswordTokenException.class);

        verify(userRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(any());
    }
}