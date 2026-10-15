package com.jenitaroselin.personalfinancemanagement.controller;

import com.jenitaroselin.personalfinancemanagement.dto.response.*;
import com.jenitaroselin.personalfinancemanagement.service.DashboardService;
import com.jenitaroselin.personalfinancemanagement.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

// Exposes aggregated metrics, monthly snapshots, category breakdowns, and dashboard overviews.
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/summary")
    public ResponseEntity<DashboardSummaryResponse> getSummary() {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(dashboardService.getSummary(userId));
    }

    @GetMapping("/monthly-summary")
    public ResponseEntity<MonthlySummaryResponse> getMonthlySummary(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate month) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(dashboardService.getMonthlySummary(userId, month));
    }

    @GetMapping("/recent-transactions")
    public ResponseEntity<List<TransactionResponse>> getRecentTransactions(
            @RequestParam(defaultValue = "10") int limit) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(dashboardService.getRecentTransactions(userId, limit));
    }

    @GetMapping("/category-spending")
    public ResponseEntity<List<CategorySpendingResponse>> getCategorySpending(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(dashboardService.getCategorySpending(userId, startDate, endDate));
    }

    @GetMapping("/budget-status")
    public ResponseEntity<List<BudgetStatusResponse>> getBudgetStatus(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate month) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(dashboardService.getBudgetStatus(userId, month));
    }

    @GetMapping("/upcoming")
    public ResponseEntity<List<SavingsGoalResponse>> getUpcoming() {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(dashboardService.getUpcoming(userId));
    }
}
