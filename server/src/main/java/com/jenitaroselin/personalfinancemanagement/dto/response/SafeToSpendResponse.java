package com.jenitaroselin.personalfinancemanagement.dto.response;

import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SafeToSpendResponse {
    private BigDecimal safeToSpend;
    private BigDecimal recordedBalance;
    private BigDecimal committedExpenses;
    private BigDecimal committedSavings;
    private BigDecimal totalCommitted;
    private String message;
}
