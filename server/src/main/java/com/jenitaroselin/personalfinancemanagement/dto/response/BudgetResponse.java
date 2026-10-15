package com.jenitaroselin.personalfinancemanagement.dto.response;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BudgetResponse {
    private Long budgetId;
    private Long categoryId;
    private String categoryName;
    private LocalDate budgetMonth;
    private BigDecimal limitAmount;
    private OffsetDateTime createdAt;
}
