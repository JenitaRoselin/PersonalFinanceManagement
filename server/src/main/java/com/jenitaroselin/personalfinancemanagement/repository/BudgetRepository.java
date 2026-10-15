package com.jenitaroselin.personalfinancemanagement.repository;

import com.jenitaroselin.personalfinancemanagement.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {

    List<Budget> findByUser_UserId(Long userId);

    List<Budget> findByUser_UserIdAndBudgetMonth(Long userId, LocalDate budgetMonth);

    Optional<Budget> findByBudgetIdAndUser_UserId(Long budgetId, Long userId);

    Optional<Budget> findByUser_UserIdAndCategory_CategoryIdAndBudgetMonth(
            Long userId, Long categoryId, LocalDate budgetMonth);

    boolean existsByUser_UserIdAndCategory_CategoryIdAndBudgetMonth(
            Long userId, Long categoryId, LocalDate budgetMonth);
}
