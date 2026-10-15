package com.jenitaroselin.personalfinancemanagement.repository;

import com.jenitaroselin.personalfinancemanagement.entity.SavingsGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SavingsGoalRepository extends JpaRepository<SavingsGoal, Long> {
    List<SavingsGoal> findByUser_UserId(Long userId);
    Optional<SavingsGoal> findByGoalIdAndUser_UserId(Long goalId, Long userId);
}
