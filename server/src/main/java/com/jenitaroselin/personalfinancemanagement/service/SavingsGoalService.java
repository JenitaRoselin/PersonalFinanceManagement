package com.jenitaroselin.personalfinancemanagement.service;

import com.jenitaroselin.personalfinancemanagement.dto.request.SavingsGoalRequest;
import com.jenitaroselin.personalfinancemanagement.dto.response.SavingsGoalResponse;
import com.jenitaroselin.personalfinancemanagement.entity.SavingsGoal;
import com.jenitaroselin.personalfinancemanagement.entity.User;
import com.jenitaroselin.personalfinancemanagement.enums.GoalStatus;
import com.jenitaroselin.personalfinancemanagement.exception.BadRequestException;
import com.jenitaroselin.personalfinancemanagement.exception.ResourceNotFoundException;
import com.jenitaroselin.personalfinancemanagement.repository.SavingsGoalRepository;
import com.jenitaroselin.personalfinancemanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

// Manages savings targets, progress increments, and status transitions.
@Service
@RequiredArgsConstructor
public class SavingsGoalService {

    private final SavingsGoalRepository savingsGoalRepository;
    private final UserRepository userRepository;

    @Transactional
    public SavingsGoalResponse createGoal(Long userId, SavingsGoalRequest request) {
        if (request.getTargetAmount() == null || request.getTargetAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Target amount must be greater than zero");
        }

        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        SavingsGoal goal = SavingsGoal.builder()
                .user(user)
                .name(request.getName().trim())
                .targetAmount(request.getTargetAmount())
                .currentAmount(request.getCurrentAmount() != null ? request.getCurrentAmount() : BigDecimal.ZERO)
                .targetDate(request.getTargetDate())
                .status(request.getStatus() != null ? request.getStatus() : GoalStatus.ACTIVE)
                .build();

        goal = savingsGoalRepository.save(goal);
        return mapToResponse(goal);
    }

    @Transactional(readOnly = true)
    public List<SavingsGoalResponse> getAllGoals(Long userId) {
        return savingsGoalRepository.findByUser_UserId(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SavingsGoalResponse getGoalById(Long userId, Long goalId) {
        SavingsGoal goal = savingsGoalRepository.findByGoalIdAndUser_UserId(goalId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Savings goal not found with id: " + goalId));
        return mapToResponse(goal);
    }

    @Transactional
    public SavingsGoalResponse updateGoal(Long userId, Long goalId, SavingsGoalRequest request) {
        SavingsGoal goal = savingsGoalRepository.findByGoalIdAndUser_UserId(goalId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Savings goal not found with id: " + goalId));

        if (request.getTargetAmount() == null || request.getTargetAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Target amount must be greater than zero");
        }

        goal.setName(request.getName().trim());
        goal.setTargetAmount(request.getTargetAmount());
        if (request.getCurrentAmount() != null) {
            goal.setCurrentAmount(request.getCurrentAmount());
        }
        goal.setTargetDate(request.getTargetDate());
        if (request.getStatus() != null) {
            goal.setStatus(request.getStatus());
        }

        goal = savingsGoalRepository.save(goal);
        return mapToResponse(goal);
    }

    @Transactional
    public void deleteGoal(Long userId, Long goalId) {
        SavingsGoal goal = savingsGoalRepository.findByGoalIdAndUser_UserId(goalId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Savings goal not found with id: " + goalId));
        savingsGoalRepository.delete(goal);
    }

    @Transactional
    public SavingsGoalResponse updateProgress(Long userId, Long goalId, BigDecimal amount, String operation) {
        SavingsGoal goal = savingsGoalRepository.findByGoalIdAndUser_UserId(goalId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Savings goal not found with id: " + goalId));

        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("Amount must be non-negative");
        }

        BigDecimal updatedAmount;
        if ("ADD".equalsIgnoreCase(operation)) {
            updatedAmount = goal.getCurrentAmount().add(amount);
        } else if ("SUBTRACT".equalsIgnoreCase(operation)) {
            updatedAmount = goal.getCurrentAmount().subtract(amount);
            if (updatedAmount.compareTo(BigDecimal.ZERO) < 0) {
                updatedAmount = BigDecimal.ZERO;
            }
        } else {
            updatedAmount = amount;
        }

        goal.setCurrentAmount(updatedAmount);
        if (goal.getCurrentAmount().compareTo(goal.getTargetAmount()) >= 0) {
            goal.setStatus(GoalStatus.COMPLETED);
        }

        goal = savingsGoalRepository.save(goal);
        return mapToResponse(goal);
    }

    @Transactional
    public SavingsGoalResponse updateStatus(Long userId, Long goalId, GoalStatus status) {
        SavingsGoal goal = savingsGoalRepository.findByGoalIdAndUser_UserId(goalId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Savings goal not found with id: " + goalId));

        goal.setStatus(status);
        goal = savingsGoalRepository.save(goal);
        return mapToResponse(goal);
    }

    private SavingsGoalResponse mapToResponse(SavingsGoal goal) {
        double percentage = 0.0;
        if (goal.getTargetAmount().compareTo(BigDecimal.ZERO) > 0) {
            percentage = goal.getCurrentAmount().multiply(BigDecimal.valueOf(100))
                    .divide(goal.getTargetAmount(), 2, RoundingMode.HALF_UP)
                    .doubleValue();
        }

        return SavingsGoalResponse.builder()
                .goalId(goal.getGoalId())
                .name(goal.getName())
                .targetAmount(goal.getTargetAmount())
                .currentAmount(goal.getCurrentAmount())
                .percentageComplete(percentage)
                .targetDate(goal.getTargetDate())
                .status(goal.getStatus())
                .createdAt(goal.getCreatedAt())
                .build();
    }
}
