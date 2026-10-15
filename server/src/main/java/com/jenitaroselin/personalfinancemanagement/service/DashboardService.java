package com.jenitaroselin.personalfinancemanagement.service;

import com.jenitaroselin.personalfinancemanagement.dto.response.*;
import com.jenitaroselin.personalfinancemanagement.enums.TransactionType;
import com.jenitaroselin.personalfinancemanagement.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// Aggregates net balances, monthly summaries, budget health, and recent transactions for user dashboards.
@Service
@RequiredArgsConstructor
public class DashboardService {

    private final TransactionRepository transactionRepository;
    private final TransactionService transactionService;
    private final BudgetService budgetService;
    private final SavingsGoalService savingsGoalService;

    @Transactional(readOnly = true)
    public DashboardSummaryResponse getSummary(Long userId) {
        BigDecimal totalIncome = transactionRepository.sumAmountByUserAndType(userId, TransactionType.INCOME);
        BigDecimal totalExpense = transactionRepository.sumAmountByUserAndType(userId, TransactionType.EXPENSE);

        if (totalIncome == null) totalIncome = BigDecimal.ZERO;
        if (totalExpense == null) totalExpense = BigDecimal.ZERO;

        BigDecimal totalBalance = totalIncome.subtract(totalExpense);

        LocalDate now = LocalDate.now();
        LocalDate startOfMonth = now.withDayOfMonth(1);
        LocalDate endOfMonth = startOfMonth.plusMonths(1).minusDays(1);

        BigDecimal monthIncome = transactionRepository.sumAmountByUserAndTypeAndDateRange(
                userId, TransactionType.INCOME, startOfMonth, endOfMonth);
        BigDecimal monthExpense = transactionRepository.sumAmountByUserAndTypeAndDateRange(
                userId, TransactionType.EXPENSE, startOfMonth, endOfMonth);

        if (monthIncome == null) monthIncome = BigDecimal.ZERO;
        if (monthExpense == null) monthExpense = BigDecimal.ZERO;

        BigDecimal monthBalance = monthIncome.subtract(monthExpense);

        return DashboardSummaryResponse.builder()
                .totalBalance(totalBalance)
                .totalIncome(totalIncome)
                .totalExpense(totalExpense)
                .currentMonthIncome(monthIncome)
                .currentMonthExpense(monthExpense)
                .currentMonthBalance(monthBalance)
                .build();
    }

    @Transactional(readOnly = true)
    public MonthlySummaryResponse getMonthlySummary(Long userId, LocalDate month) {
        LocalDate startOfMonth = (month != null ? month : LocalDate.now()).withDayOfMonth(1);
        LocalDate endOfMonth = startOfMonth.plusMonths(1).minusDays(1);

        BigDecimal income = transactionRepository.sumAmountByUserAndTypeAndDateRange(
                userId, TransactionType.INCOME, startOfMonth, endOfMonth);
        BigDecimal expense = transactionRepository.sumAmountByUserAndTypeAndDateRange(
                userId, TransactionType.EXPENSE, startOfMonth, endOfMonth);

        if (income == null) income = BigDecimal.ZERO;
        if (expense == null) expense = BigDecimal.ZERO;

        return MonthlySummaryResponse.builder()
                .month(startOfMonth)
                .totalIncome(income)
                .totalExpense(expense)
                .netBalance(income.subtract(expense))
                .build();
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> getRecentTransactions(Long userId, int limit) {
        List<TransactionResponse> transactions = transactionService.getRecentTransactions(userId);
        if (transactions.size() > limit) {
            return transactions.subList(0, limit);
        }
        return transactions;
    }

    @Transactional(readOnly = true)
    public List<CategorySpendingResponse> getCategorySpending(Long userId, LocalDate startDate, LocalDate endDate) {
        if (startDate == null) {
            startDate = LocalDate.now().withDayOfMonth(1);
        }
        if (endDate == null) {
            endDate = startDate.plusMonths(1).minusDays(1);
        }

        List<Object[]> rawList = transactionRepository.sumSpendingByCategory(userId, startDate, endDate);
        List<CategorySpendingResponse> result = new ArrayList<>();

        BigDecimal totalSpending = BigDecimal.ZERO;
        for (Object[] row : rawList) {
            BigDecimal amt = (BigDecimal) row[2];
            if (amt != null) {
                totalSpending = totalSpending.add(amt);
            }
        }

        for (Object[] row : rawList) {
            Long catId = (Long) row[0];
            String catName = (String) row[1];
            BigDecimal amt = (BigDecimal) row[2];
            double percentage = 0.0;
            if (totalSpending.compareTo(BigDecimal.ZERO) > 0 && amt != null) {
                percentage = amt.multiply(BigDecimal.valueOf(100))
                        .divide(totalSpending, 2, RoundingMode.HALF_UP)
                        .doubleValue();
            }
            result.add(CategorySpendingResponse.builder()
                    .categoryId(catId)
                    .categoryName(catName)
                    .totalSpent(amt != null ? amt : BigDecimal.ZERO)
                    .percentage(percentage)
                    .build());
        }

        return result;
    }

    @Transactional(readOnly = true)
    public List<BudgetStatusResponse> getBudgetStatus(Long userId, LocalDate month) {
        LocalDate queryMonth = month != null ? month : LocalDate.now();
        return budgetService.getBudgetsForMonth(userId, queryMonth);
    }

    @Transactional(readOnly = true)
    public List<SavingsGoalResponse> getUpcoming(Long userId) {
        return savingsGoalService.getAllGoals(userId);
    }
}
