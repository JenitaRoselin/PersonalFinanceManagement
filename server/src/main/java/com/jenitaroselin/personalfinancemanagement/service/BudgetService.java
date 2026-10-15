package com.jenitaroselin.personalfinancemanagement.service;

import com.jenitaroselin.personalfinancemanagement.dto.request.BudgetRequest;
import com.jenitaroselin.personalfinancemanagement.dto.response.BudgetResponse;
import com.jenitaroselin.personalfinancemanagement.dto.response.BudgetStatusResponse;
import com.jenitaroselin.personalfinancemanagement.entity.Budget;
import com.jenitaroselin.personalfinancemanagement.entity.Category;
import com.jenitaroselin.personalfinancemanagement.entity.User;
import com.jenitaroselin.personalfinancemanagement.enums.CategoryType;
import com.jenitaroselin.personalfinancemanagement.exception.BadRequestException;
import com.jenitaroselin.personalfinancemanagement.exception.DuplicateResourceException;
import com.jenitaroselin.personalfinancemanagement.exception.ResourceNotFoundException;
import com.jenitaroselin.personalfinancemanagement.repository.BudgetRepository;
import com.jenitaroselin.personalfinancemanagement.repository.CategoryRepository;
import com.jenitaroselin.personalfinancemanagement.repository.TransactionRepository;
import com.jenitaroselin.personalfinancemanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

// Calculates dynamic spending against monthly category budgets and detects threshold overages.
@Service
@RequiredArgsConstructor
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    @Transactional
    public BudgetResponse createBudget(Long userId, BudgetRequest request) {
        LocalDate normalizedMonth = request.getBudgetMonth().withDayOfMonth(1);

        if (request.getLimitAmount() == null || request.getLimitAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Budget limit must be greater than zero");
        }

        Category category = categoryRepository.findByCategoryIdAndUser_UserId(request.getCategoryId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));

        if (category.getType() != CategoryType.EXPENSE) {
            throw new BadRequestException("Budgets can only be created for EXPENSE categories");
        }

        if (budgetRepository.existsByUser_UserIdAndCategory_CategoryIdAndBudgetMonth(userId, request.getCategoryId(), normalizedMonth)) {
            throw new DuplicateResourceException("A budget for this category and month already exists");
        }

        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Budget budget = Budget.builder()
                .user(user)
                .category(category)
                .budgetMonth(normalizedMonth)
                .limitAmount(request.getLimitAmount())
                .build();

        budget = budgetRepository.save(budget);
        return mapToResponse(budget);
    }

    @Transactional(readOnly = true)
    public List<BudgetResponse> getAllBudgets(Long userId) {
        return budgetRepository.findByUser_UserId(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BudgetStatusResponse> getBudgetsForMonth(Long userId, LocalDate month) {
        LocalDate normalizedMonth = month.withDayOfMonth(1);
        List<Budget> budgets = budgetRepository.findByUser_UserIdAndBudgetMonth(userId, normalizedMonth);

        return budgets.stream()
                .map(budget -> calculateBudgetStatus(userId, budget))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BudgetResponse getBudgetById(Long userId, Long budgetId) {
        Budget budget = budgetRepository.findByBudgetIdAndUser_UserId(budgetId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found with id: " + budgetId));
        return mapToResponse(budget);
    }

    @Transactional(readOnly = true)
    public BudgetStatusResponse getBudgetStatus(Long userId, Long budgetId) {
        Budget budget = budgetRepository.findByBudgetIdAndUser_UserId(budgetId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found with id: " + budgetId));
        return calculateBudgetStatus(userId, budget);
    }

    @Transactional
    public BudgetResponse updateBudget(Long userId, Long budgetId, BudgetRequest request) {
        Budget budget = budgetRepository.findByBudgetIdAndUser_UserId(budgetId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found with id: " + budgetId));

        if (request.getLimitAmount() == null || request.getLimitAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Budget limit must be greater than zero");
        }

        budget.setLimitAmount(request.getLimitAmount());
        budget = budgetRepository.save(budget);
        return mapToResponse(budget);
    }

    @Transactional
    public void deleteBudget(Long userId, Long budgetId) {
        Budget budget = budgetRepository.findByBudgetIdAndUser_UserId(budgetId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found with id: " + budgetId));
        budgetRepository.delete(budget);
    }

    private BudgetStatusResponse calculateBudgetStatus(Long userId, Budget budget) {
        LocalDate startDate = budget.getBudgetMonth();
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);

        BigDecimal spent = transactionRepository.sumExpensesByUserAndCategoryAndDateRange(
                userId, budget.getCategory().getCategoryId(), startDate, endDate);

        if (spent == null) {
            spent = BigDecimal.ZERO;
        }

        BigDecimal remaining = budget.getLimitAmount().subtract(spent);

        double percentageUsed = 0.0;
        if (budget.getLimitAmount().compareTo(BigDecimal.ZERO) > 0) {
            percentageUsed = spent.multiply(BigDecimal.valueOf(100))
                    .divide(budget.getLimitAmount(), 2, RoundingMode.HALF_UP)
                    .doubleValue();
        }

        String status;
        if (percentageUsed < 70.0) {
            status = "NORMAL";
        } else if (percentageUsed <= 90.0) {
            status = "APPROACHING_LIMIT";
        } else if (percentageUsed <= 100.0) {
            status = "NEAR_LIMIT";
        } else {
            status = "OVER_BUDGET";
        }

        return BudgetStatusResponse.builder()
                .budgetId(budget.getBudgetId())
                .categoryId(budget.getCategory().getCategoryId())
                .categoryName(budget.getCategory().getName())
                .budgetMonth(budget.getBudgetMonth())
                .limitAmount(budget.getLimitAmount())
                .amountSpent(spent)
                .remainingBudget(remaining)
                .percentageUsed(percentageUsed)
                .status(status)
                .build();
    }

    private BudgetResponse mapToResponse(Budget budget) {
        return BudgetResponse.builder()
                .budgetId(budget.getBudgetId())
                .categoryId(budget.getCategory().getCategoryId())
                .categoryName(budget.getCategory().getName())
                .budgetMonth(budget.getBudgetMonth())
                .limitAmount(budget.getLimitAmount())
                .createdAt(budget.getCreatedAt())
                .build();
    }
}
