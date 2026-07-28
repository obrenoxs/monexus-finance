package com.monexus.finance.user.controller;

import com.monexus.finance.user.dto.request.LoginRequest;
import com.monexus.finance.user.dto.request.RegisterRequest;
import com.monexus.finance.user.dto.response.AuthResponse;
import com.monexus.finance.user.dto.response.RegisterResponse;
import com.monexus.finance.user.dto.response.UserResponse;
import com.monexus.finance.user.service.AuthService;
import com.monexus.finance.user.service.EmailConfirmationService;
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

    public AuthController(UserService userService, AuthService authService, EmailConfirmationService emailConfirmationService) {
        this.userService = userService;
        this.authService = authService;
        this.emailConfirmationService = emailConfirmationService;
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
 }
