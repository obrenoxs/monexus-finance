package com.monexus.finance.user.controller;

import com.monexus.finance.user.dto.request.ChangeEmailRequest;
import com.monexus.finance.user.dto.request.DeleteAccountRequest;
import com.monexus.finance.user.dto.request.UpdateProfileRequest;
import com.monexus.finance.user.dto.response.UserResponse;
import com.monexus.finance.user.security.CustomUserDetails;
import com.monexus.finance.user.service.EmailChangeService;
import com.monexus.finance.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;
    private final EmailChangeService emailChangeService;

    public UserController(UserService userService, EmailChangeService emailChangeService) {
        this.userService = userService;
        this.emailChangeService = emailChangeService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
        UserResponse response = userService.getUserResponse(userDetails.getUser());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateCurrentUser(@AuthenticationPrincipal CustomUserDetails userDetails, @Valid @RequestBody UpdateProfileRequest request) {
        UserResponse response = userService.updateProfile(userDetails.getUser(), request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping(value = "/me/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserResponse> updateProfileImage(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestParam("file") MultipartFile file) {
        UserResponse response = userService.updateProfileImage(userDetails.getUser(), file);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/me/change-email")
    public ResponseEntity<Map<String, String>> changeEmail(@AuthenticationPrincipal CustomUserDetails userDetails, @Valid @RequestBody ChangeEmailRequest request) {
        emailChangeService.requestEmailChange(userDetails.getUser(), request.newEmail(), request.currentPassword());
        return ResponseEntity.ok(Map.of("message", "Enviamos um link de confirmação para o novo e-mail informado."));
    }

    @GetMapping("/me/confirm-email-change")
    public ResponseEntity<Map<String, String>> confirmEmailChange(@RequestParam("token") String token) {
        emailChangeService.confirmEmailChange(token);
        return ResponseEntity.ok(Map.of("message", "E-mail alterado com sucesso!"));
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteAccount(@AuthenticationPrincipal CustomUserDetails userDetails, @Valid @RequestBody DeleteAccountRequest request) {
        userService.deleteAccount(userDetails.getUser(), request.currentPassword());
        return ResponseEntity.noContent().build();
    }
}
