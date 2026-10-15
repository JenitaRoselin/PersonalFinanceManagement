package com.jenitaroselin.personalfinancemanagement.service;

import com.jenitaroselin.personalfinancemanagement.dto.response.*;
import com.jenitaroselin.personalfinancemanagement.entity.Transaction;
import com.jenitaroselin.personalfinancemanagement.enums.TransactionType;
import com.jenitaroselin.personalfinancemanagement.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// Generates analytical spending reports, monthly trend comparisons, and category distributions.
@Service
@RequiredArgsConstructor
public class ReportService {

    private final TransactionRepository transactionRepository;
    private final DashboardService dashboardService;
    private final BudgetService budgetService;

    @Transactional(readOnly = true)
    public ReportResponse getMonthlyReport(Long userId, Integer year) {
        int targetYear = year != null ? year : LocalDate.now().getYear();
        LocalDate startDate = LocalDate.of(targetYear, 1, 1);
        LocalDate endDate = LocalDate.of(targetYear, 12, 31);

        BigDecimal totalIncome = transactionRepository.sumAmountByUserAndTypeAndDateRange(
                userId, TransactionType.INCOME, startDate, endDate);
        BigDecimal totalExpense = transactionRepository.sumAmountByUserAndTypeAndDateRange(
                userId, TransactionType.EXPENSE, startDate, endDate);

        if (totalIncome == null) totalIncome = BigDecimal.ZERO;
        if (totalExpense == null) totalExpense = BigDecimal.ZERO;

        BigDecimal netSavings = totalIncome.subtract(totalExpense);
        double savingsRate = 0.0;
        if (totalIncome.compareTo(BigDecimal.ZERO) > 0) {
            savingsRate = netSavings.multiply(BigDecimal.valueOf(100))
                    .divide(totalIncome, 2, RoundingMode.HALF_UP)
                    .doubleValue();
        }

        List<MonthlySummaryResponse> monthlyTrend = getIncomeVsExpenseTrend(userId, 12);
        List<CategorySpendingResponse> categoryBreakdown = dashboardService.getCategorySpending(userId, startDate, endDate);
        List<TransactionResponse> topExpenses = getTopExpenses(userId, 5);

        return ReportResponse.builder()
                .period(String.valueOf(targetYear))
                .totalIncome(totalIncome)
                .totalExpenses(totalExpense)
                .netSavings(netSavings)
                .savingsRate(savingsRate)
                .monthlyTrend(monthlyTrend)
                .categoryBreakdown(categoryBreakdown)
                .topExpenses(topExpenses)
                .build();
    }

    @Transactional(readOnly = true)
    public List<MonthlySummaryResponse> getIncomeVsExpenseTrend(Long userId, int months) {
        List<MonthlySummaryResponse> trend = new ArrayList<>();
        LocalDate current = LocalDate.now().withDayOfMonth(1);

        for (int i = months - 1; i >= 0; i--) {
            LocalDate monthStart = current.minusMonths(i);
            LocalDate monthEnd = monthStart.plusMonths(1).minusDays(1);

            BigDecimal income = transactionRepository.sumAmountByUserAndTypeAndDateRange(
                    userId, TransactionType.INCOME, monthStart, monthEnd);
            BigDecimal expense = transactionRepository.sumAmountByUserAndTypeAndDateRange(
                    userId, TransactionType.EXPENSE, monthStart, monthEnd);

            if (income == null) income = BigDecimal.ZERO;
            if (expense == null) expense = BigDecimal.ZERO;

            trend.add(MonthlySummaryResponse.builder()
                    .month(monthStart)
                    .totalIncome(income)
                    .totalExpense(expense)
                    .netBalance(income.subtract(expense))
                    .build());
        }
        return trend;
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> getTopExpenses(Long userId, int limit) {
        List<Transaction> transactions = transactionRepository.findTopExpensesByUser(
                userId, PageRequest.of(0, limit));

        return transactions.stream().map(t -> TransactionResponse.builder()
                .transactionId(t.getTransactionId())
                .type(t.getType())
                .amount(t.getAmount())
                .transactionDate(t.getTransactionDate())
                .description(t.getDescription())
                .categoryId(t.getCategory().getCategoryId())
                .categoryName(t.getCategory().getName())
                .paymentMethodId(t.getPaymentMethod() != null ? t.getPaymentMethod().getPaymentMethodId() : null)
                .paymentMethodName(t.getPaymentMethod() != null ? t.getPaymentMethod().getName() : null)
                .createdAt(t.getCreatedAt())
                .updatedAt(t.getUpdatedAt())
                .build()).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BudgetStatusResponse> getBudgetPerformance(Long userId, LocalDate month) {
        LocalDate queryMonth = month != null ? month : LocalDate.now().withDayOfMonth(1);
        return budgetService.getBudgetsForMonth(userId, queryMonth);
    }
}
