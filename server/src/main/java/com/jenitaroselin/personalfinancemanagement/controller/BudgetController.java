package com.jenitaroselin.personalfinancemanagement.controller;

import com.jenitaroselin.personalfinancemanagement.dto.request.BudgetRequest;
import com.jenitaroselin.personalfinancemanagement.dto.response.BudgetResponse;
import com.jenitaroselin.personalfinancemanagement.dto.response.BudgetStatusResponse;
import com.jenitaroselin.personalfinancemanagement.service.BudgetService;
import com.jenitaroselin.personalfinancemanagement.util.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

// Exposes REST endpoints for monthly budget configuration and real-time utilization status.
@RestController
@RequestMapping("/api/budgets")
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;

    @PostMapping
    public ResponseEntity<BudgetResponse> createBudget(@Valid @RequestBody BudgetRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        BudgetResponse response = budgetService.createBudget(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<BudgetResponse>> getAllBudgets() {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(budgetService.getAllBudgets(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BudgetResponse> getBudgetById(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(budgetService.getBudgetById(userId, id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BudgetResponse> updateBudget(
            @PathVariable Long id,
            @Valid @RequestBody BudgetRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(budgetService.updateBudget(userId, id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBudget(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        budgetService.deleteBudget(userId, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/month/{month}")
    public ResponseEntity<List<BudgetStatusResponse>> getBudgetsForMonth(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate month) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(budgetService.getBudgetsForMonth(userId, month));
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<BudgetStatusResponse> getBudgetStatus(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(budgetService.getBudgetStatus(userId, id));
    }
}
