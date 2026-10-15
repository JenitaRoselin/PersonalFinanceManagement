package com.jenitaroselin.personalfinancemanagement.service;

import com.jenitaroselin.personalfinancemanagement.dto.response.AlertResponse;
import com.jenitaroselin.personalfinancemanagement.dto.response.BudgetStatusResponse;
import com.jenitaroselin.personalfinancemanagement.dto.response.SafeToSpendResponse;
import com.jenitaroselin.personalfinancemanagement.dto.response.SavingsGoalResponse;
import com.jenitaroselin.personalfinancemanagement.entity.Transaction;
import com.jenitaroselin.personalfinancemanagement.enums.GoalStatus;
import com.jenitaroselin.personalfinancemanagement.enums.TransactionType;
import com.jenitaroselin.personalfinancemanagement.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// Computes safe-to-spend allowances, unusual expense warnings, and budget proximity alerts.
@Service
@RequiredArgsConstructor
public class FinancialInsightService {

    private final TransactionRepository transactionRepository;
    private final BudgetService budgetService;
    private final SavingsGoalService savingsGoalService;

    @Transactional(readOnly = true)
    public SafeToSpendResponse getSafeToSpend(Long userId, LocalDate month) {
        LocalDate queryMonth = month != null ? month : LocalDate.now().withDayOfMonth(1);

        BigDecimal totalIncome = transactionRepository.sumAmountByUserAndType(userId, TransactionType.INCOME);
        BigDecimal totalExpense = transactionRepository.sumAmountByUserAndType(userId, TransactionType.EXPENSE);

        if (totalIncome == null) totalIncome = BigDecimal.ZERO;
        if (totalExpense == null) totalExpense = BigDecimal.ZERO;

        BigDecimal recordedBalance = totalIncome.subtract(totalExpense);

        // Calculate committed expenses: remaining budget for current month across all category budgets
        List<BudgetStatusResponse> budgets = budgetService.getBudgetsForMonth(userId, queryMonth);
        BigDecimal committedExpenses = BigDecimal.ZERO;
        for (BudgetStatusResponse b : budgets) {
            if (b.getRemainingBudget().compareTo(BigDecimal.ZERO) > 0) {
                committedExpenses = committedExpenses.add(b.getRemainingBudget());
            }
        }

        // Calculate committed savings: remaining target for ACTIVE savings goals
        List<SavingsGoalResponse> goals = savingsGoalService.getAllGoals(userId);
        BigDecimal committedSavings = BigDecimal.ZERO;
        for (SavingsGoalResponse g : goals) {
            if (g.getStatus() == GoalStatus.ACTIVE) {
                BigDecimal remainingGoal = g.getTargetAmount().subtract(g.getCurrentAmount());
                if (remainingGoal.compareTo(BigDecimal.ZERO) > 0) {
                    committedSavings = committedSavings.add(remainingGoal);
                }
            }
        }

        BigDecimal totalCommitted = committedExpenses.add(committedSavings);
        BigDecimal safeToSpend = recordedBalance.subtract(totalCommitted);

        String message = String.format("You have ₹%,.0f recorded balance, but ₹%,.0f is already committed to upcoming expenses and savings.",
                recordedBalance.doubleValue(), totalCommitted.doubleValue());

        return SafeToSpendResponse.builder()
                .safeToSpend(safeToSpend)
                .recordedBalance(recordedBalance)
                .committedExpenses(committedExpenses)
                .committedSavings(committedSavings)
                .totalCommitted(totalCommitted)
                .message(message)
                .build();
    }

    @Transactional(readOnly = true)
    public List<AlertResponse> getSpendingAlerts(Long userId) {
        List<AlertResponse> alerts = new ArrayList<>();
        List<Transaction> topExpenses = transactionRepository.findTopExpensesByUser(userId, PageRequest.of(0, 3));

        for (Transaction t : topExpenses) {
            if (t.getAmount().compareTo(BigDecimal.valueOf(5000)) > 0) {
                alerts.add(AlertResponse.builder()
                        .type("INFO")
                        .title("High Expense Recorded")
                        .message(String.format("Large expense of ₹%,.2f for '%s' on %s",
                                t.getAmount().doubleValue(), t.getDescription(), t.getTransactionDate()))
                        .categoryName(t.getCategory().getName())
                        .build());
            }
        }
        return alerts;
    }

    @Transactional(readOnly = true)
    public List<AlertResponse> getBudgetAlerts(Long userId, LocalDate month) {
        LocalDate queryMonth = month != null ? month : LocalDate.now().withDayOfMonth(1);
        List<BudgetStatusResponse> budgets = budgetService.getBudgetsForMonth(userId, queryMonth);
        List<AlertResponse> alerts = new ArrayList<>();

        for (BudgetStatusResponse b : budgets) {
            if ("OVER_BUDGET".equals(b.getStatus())) {
                BigDecimal overage = b.getAmountSpent().subtract(b.getLimitAmount());
                alerts.add(AlertResponse.builder()
                        .type("DANGER")
                        .title("Budget Exceeded")
                        .message(String.format("Budget for %s has been exceeded by ₹%,.2f (%.1f%% spent)",
                                b.getCategoryName(), overage.doubleValue(), b.getPercentageUsed()))
                        .categoryName(b.getCategoryName())
                        .thresholdPercentage(b.getPercentageUsed())
                        .build());
            } else if ("NEAR_LIMIT".equals(b.getStatus()) || "APPROACHING_LIMIT".equals(b.getStatus())) {
                alerts.add(AlertResponse.builder()
                        .type("WARNING")
                        .title("Approaching Budget Limit")
                        .message(String.format("Budget for %s is nearing its limit: %.1f%% used, ₹%,.2f remaining",
                                b.getCategoryName(), b.getPercentageUsed(), b.getRemainingBudget().doubleValue()))
                        .categoryName(b.getCategoryName())
                        .thresholdPercentage(b.getPercentageUsed())
                        .build());
            }
        }
        return alerts;
    }
}
