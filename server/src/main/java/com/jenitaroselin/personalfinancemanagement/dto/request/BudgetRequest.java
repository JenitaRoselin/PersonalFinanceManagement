package com.jenitaroselin.personalfinancemanagement.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BudgetRequest {

    @NotNull(message = "Category is required")
    private Long categoryId;

    @NotNull(message = "Budget month is required (e.g. 2026-10-01)")
    private LocalDate budgetMonth;

    @NotNull(message = "Limit amount is required")
    @Positive(message = "Limit amount must be greater than zero")
    private BigDecimal limitAmount;
}
