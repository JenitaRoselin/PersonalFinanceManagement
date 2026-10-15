package com.jenitaroselin.personalfinancemanagement.dto.request;

import com.jenitaroselin.personalfinancemanagement.enums.GoalStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SavingsGoalRequest {

    @NotBlank(message = "Goal name is required")
    @Size(max = 100, message = "Goal name cannot exceed 100 characters")
    private String name;

    @NotNull(message = "Target amount is required")
    @Positive(message = "Target amount must be greater than zero")
    private BigDecimal targetAmount;

    @Builder.Default
    private BigDecimal currentAmount = BigDecimal.ZERO;

    private LocalDate targetDate;

    @Builder.Default
    private GoalStatus status = GoalStatus.ACTIVE;
}
