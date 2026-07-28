package com.monexus.finance.user.controller;

import com.monexus.finance.user.dto.request.ForgotPasswordRequest;
import com.monexus.finance.user.dto.request.LoginRequest;
import com.monexus.finance.user.dto.request.RegisterRequest;
import com.monexus.finance.user.dto.request.ResetPasswordRequest;
import com.monexus.finance.user.dto.response.AuthResponse;
import com.monexus.finance.user.dto.response.RegisterResponse;
import com.monexus.finance.user.dto.response.UserResponse;
import com.monexus.finance.user.service.AuthService;
import com.monexus.finance.user.service.EmailConfirmationService;
import com.monexus.finance.user.service.PasswordResetService;
import com.monexus.finance.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserService userService;
    private final AuthService authService;
    private final EmailConfirmationService emailConfirmationService;
    private final PasswordResetService passwordResetService;

    public AuthController(UserService userService,
                          AuthService authService,
                          EmailConfirmationService emailConfirmationService,
                          PasswordResetService passwordResetService) {
        this.userService = userService;
        this.authService = authService;
        this.emailConfirmationService = emailConfirmationService;
        this.passwordResetService = passwordResetService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        UserResponse userResponse = userService.register(request);

        RegisterResponse response = new RegisterResponse(
                userResponse,
                "Cadastro realizado com sucesso! Verifique seu e-mail para confirmar sua conta."
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/confirm-email")
    public ResponseEntity<Map<String, String>> confirmEmail(@RequestParam("token") String token) {
        emailConfirmationService.confirmEmail(token);
        return ResponseEntity.ok(Map.of("message", "E-mail confirmado com sucesso!"));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        passwordResetService.requestPasswordReset(request.email());
        return ResponseEntity.ok(Map.of("message", "Se este e-mail estiver cadastrado, você receberá um link de redefinição em instantes."));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        passwordResetService.resetPassword(request.token(), request.newPassword());
        return ResponseEntity.ok(Map.of("message", "Senha redefinida com sucesso!"));
    }
 }
