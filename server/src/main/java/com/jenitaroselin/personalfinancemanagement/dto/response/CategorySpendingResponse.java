package com.jenitaroselin.personalfinancemanagement.dto.response;

import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategorySpendingResponse {
    private Long categoryId;
    private String categoryName;
    private BigDecimal totalSpent;
    private Double percentage;
}
