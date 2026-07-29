package com.monexus.finance.user.controller;

import com.monexus.finance.user.dto.request.UpdateProfileRequest;
import com.monexus.finance.user.dto.response.UserResponse;
import com.monexus.finance.user.security.CustomUserDetails;
import com.monexus.finance.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
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
}
