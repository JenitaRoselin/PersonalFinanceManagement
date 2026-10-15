package com.jenitaroselin.personalfinancemanagement.dto.response;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportResponse {
    private String period;
    private BigDecimal totalIncome;
    private BigDecimal totalExpenses;
    private BigDecimal netSavings;
    private Double savingsRate;
    private List<MonthlySummaryResponse> monthlyTrend;
    private List<CategorySpendingResponse> categoryBreakdown;
    private List<TransactionResponse> topExpenses;
}
