package com.monexus.finance.user.service;

import com.monexus.finance.shared.security.JwtService;
import com.monexus.finance.user.dto.request.LoginRequest;
import com.monexus.finance.user.dto.response.AuthResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(authenticationManager, jwtService);
    }

    @Test
    void shouldAuthenticateAndReturnGeneratedToken() {
        LoginRequest request = new LoginRequest("breno@example.com", "12345678");

        when(jwtService.generatedToken("breno@example.com")).thenReturn("fake-jwt-token");

        AuthResponse response = authService.login(request);

        assertThat(response.token()).isEqualTo("fake-jwt-token");
        assertThat(response.tokenType()).isEqualTo("Bearer");

        ArgumentCaptor<UsernamePasswordAuthenticationToken> tokenCaptor =
                ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);
        verify(authenticationManager).authenticate(tokenCaptor.capture());

        assertThat(tokenCaptor.getValue().getPrincipal()).isEqualTo("breno@example.com");
        assertThat(tokenCaptor.getValue().getCredentials()).isEqualTo("12345678");
    }

    @Test
    void shouldPropagateExceptionWhenAuthenticationFails() {
        LoginRequest request = new LoginRequest("breno@example.com", "wrong-password");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BadCredentialsException.class);

        verify(jwtService, never()).generatedToken(any());
    }
}
