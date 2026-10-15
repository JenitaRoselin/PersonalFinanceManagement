package com.jenitaroselin.personalfinancemanagement.dto.response;

import com.jenitaroselin.personalfinancemanagement.enums.TransactionType;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionResponse {
    private Long transactionId;
    private TransactionType type;
    private BigDecimal amount;
    private LocalDate transactionDate;
    private String description;
    private Long categoryId;
    private String categoryName;
    private Long paymentMethodId;
    private String paymentMethodName;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
