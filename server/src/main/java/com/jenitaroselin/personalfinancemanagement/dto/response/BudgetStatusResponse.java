package com.jenitaroselin.personalfinancemanagement.dto.response;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BudgetStatusResponse {
    private Long budgetId;
    private Long categoryId;
    private String categoryName;
    private LocalDate budgetMonth;
    private BigDecimal limitAmount;
    private BigDecimal amountSpent;
    private BigDecimal remainingBudget;
    private Double percentageUsed;
    private String status; // NORMAL, APPROACHING_LIMIT, NEAR_LIMIT, OVER_BUDGET
}
