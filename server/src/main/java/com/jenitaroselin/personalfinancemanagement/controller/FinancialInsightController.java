package com.jenitaroselin.personalfinancemanagement.controller;

import com.jenitaroselin.personalfinancemanagement.dto.response.AlertResponse;
import com.jenitaroselin.personalfinancemanagement.dto.response.SafeToSpendResponse;
import com.jenitaroselin.personalfinancemanagement.service.FinancialInsightService;
import com.jenitaroselin.personalfinancemanagement.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

// Exposes proactive financial alerts, budget threshold warnings, and safe-to-spend estimations.
@RestController
@RequestMapping("/api/insights")
@RequiredArgsConstructor
public class FinancialInsightController {

    private final FinancialInsightService insightService;

    @GetMapping("/safe-to-spend")
    public ResponseEntity<SafeToSpendResponse> getSafeToSpend(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate month) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(insightService.getSafeToSpend(userId, month));
    }

    @GetMapping("/spending-alerts")
    public ResponseEntity<List<AlertResponse>> getSpendingAlerts() {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(insightService.getSpendingAlerts(userId));
    }

    @GetMapping("/budget-alerts")
    public ResponseEntity<List<AlertResponse>> getBudgetAlerts(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate month) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(insightService.getBudgetAlerts(userId, month));
    }
}
