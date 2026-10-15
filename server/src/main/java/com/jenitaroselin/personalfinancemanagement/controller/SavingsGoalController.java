package com.jenitaroselin.personalfinancemanagement.controller;

import com.jenitaroselin.personalfinancemanagement.dto.request.SavingsGoalRequest;
import com.jenitaroselin.personalfinancemanagement.dto.response.SavingsGoalResponse;
import com.jenitaroselin.personalfinancemanagement.enums.GoalStatus;
import com.jenitaroselin.personalfinancemanagement.service.SavingsGoalService;
import com.jenitaroselin.personalfinancemanagement.util.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

// Exposes REST endpoints for goal CRUD, progress updates, and status modifications.
@RestController
@RequestMapping("/api/savings-goals")
@RequiredArgsConstructor
public class SavingsGoalController {

    private final SavingsGoalService savingsGoalService;

    @PostMapping
    public ResponseEntity<SavingsGoalResponse> createGoal(@Valid @RequestBody SavingsGoalRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        SavingsGoalResponse response = savingsGoalService.createGoal(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<SavingsGoalResponse>> getAllGoals() {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(savingsGoalService.getAllGoals(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SavingsGoalResponse> getGoalById(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(savingsGoalService.getGoalById(userId, id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SavingsGoalResponse> updateGoal(
            @PathVariable Long id,
            @Valid @RequestBody SavingsGoalRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(savingsGoalService.updateGoal(userId, id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGoal(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        savingsGoalService.deleteGoal(userId, id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/progress")
    public ResponseEntity<SavingsGoalResponse> updateProgress(
            @PathVariable Long id,
            @RequestParam BigDecimal amount,
            @RequestParam(defaultValue = "ADD") String operation) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(savingsGoalService.updateProgress(userId, id, amount, operation));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<SavingsGoalResponse> updateStatus(
            @PathVariable Long id,
            @RequestParam GoalStatus status) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(savingsGoalService.updateStatus(userId, id, status));
    }
}
