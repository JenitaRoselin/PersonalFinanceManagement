package com.jenitaroselin.personalfinancemanagement.dto.response;

import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardSummaryResponse {
    private BigDecimal totalBalance;
    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private BigDecimal currentMonthIncome;
    private BigDecimal currentMonthExpense;
    private BigDecimal currentMonthBalance;
}
