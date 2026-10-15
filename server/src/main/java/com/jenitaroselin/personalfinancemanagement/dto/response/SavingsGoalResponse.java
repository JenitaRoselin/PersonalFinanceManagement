package com.jenitaroselin.personalfinancemanagement.dto.response;

import com.jenitaroselin.personalfinancemanagement.enums.GoalStatus;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SavingsGoalResponse {
    private Long goalId;
    private String name;
    private BigDecimal targetAmount;
    private BigDecimal currentAmount;
    private Double percentageComplete;
    private LocalDate targetDate;
    private GoalStatus status;
    private OffsetDateTime createdAt;
}
