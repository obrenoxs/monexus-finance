package com.monexus.finance.goal.controller;

import com.monexus.finance.goal.dto.request.GoalProgressRequest;
import com.monexus.finance.goal.dto.request.GoalRequest;
import com.monexus.finance.goal.dto.response.GoalResponse;
import com.monexus.finance.goal.service.GoalService;
import com.monexus.finance.user.security.CustomUserDetails;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/goals")
public class GoalController {

    private final GoalService goalService;

    public GoalController(GoalService goalService) {
        this.goalService = goalService;
    }

    @PostMapping
    public ResponseEntity<GoalResponse> createGoal(@AuthenticationPrincipal CustomUserDetails userDetails, @Valid @RequestBody GoalRequest request) {
        GoalResponse response = goalService.createGoal(userDetails.getUser(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<GoalResponse>> getGoals(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<GoalResponse> response = goalService.getGoals(userDetails.getUser());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GoalResponse> getGoalById(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long id) {
        GoalResponse response = goalService.getGoalById(userDetails.getUser(), id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GoalResponse> updateGoal(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long id, @Valid @RequestBody GoalRequest request) {
        GoalResponse response = goalService.updateGoal(userDetails.getUser(), id, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/progress")
    public ResponseEntity<GoalResponse> updateProgress(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long id, @Valid @RequestBody GoalProgressRequest request) {
        GoalResponse response = goalService.updateProgress(userDetails.getUser(), id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGoal(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long id) {
        goalService.deleteGoal(userDetails.getUser(), id);
        return ResponseEntity.noContent().build();
    }
}
