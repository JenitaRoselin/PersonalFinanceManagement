package com.jenitaroselin.personalfinancemanagement.controller;

import com.jenitaroselin.personalfinancemanagement.dto.response.*;
import com.jenitaroselin.personalfinancemanagement.service.DashboardService;
import com.jenitaroselin.personalfinancemanagement.service.ReportService;
import com.jenitaroselin.personalfinancemanagement.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

// Exposes analytical reports covering monthly comparisons, category spending, and top expenses.
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;
    private final DashboardService dashboardService;

    @GetMapping("/monthly")
    public ResponseEntity<ReportResponse> getMonthlyReport(@RequestParam(required = false) Integer year) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(reportService.getMonthlyReport(userId, year));
    }

    @GetMapping("/category")
    public ResponseEntity<List<CategorySpendingResponse>> getCategorySpendingReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(dashboardService.getCategorySpending(userId, startDate, endDate));
    }

    @GetMapping("/income-vs-expense")
    public ResponseEntity<List<MonthlySummaryResponse>> getIncomeVsExpense(
            @RequestParam(defaultValue = "6") int months) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(reportService.getIncomeVsExpenseTrend(userId, months));
    }

    @GetMapping("/top-expenses")
    public ResponseEntity<List<TransactionResponse>> getTopExpenses(
            @RequestParam(defaultValue = "10") int limit) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(reportService.getTopExpenses(userId, limit));
    }

    @GetMapping("/trends")
    public ResponseEntity<List<MonthlySummaryResponse>> getTrends(
            @RequestParam(defaultValue = "6") int months) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(reportService.getIncomeVsExpenseTrend(userId, months));
    }

    @GetMapping("/budget-performance")
    public ResponseEntity<List<BudgetStatusResponse>> getBudgetPerformance(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate month) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(reportService.getBudgetPerformance(userId, month));
    }
}
