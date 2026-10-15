package com.jenitaroselin.personalfinancemanagement.dto.request;

import com.jenitaroselin.personalfinancemanagement.enums.TransactionType;
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
public class TransactionRequest {

    @NotNull(message = "Transaction type is required (INCOME or EXPENSE)")
    private TransactionType type;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be greater than zero")
    private BigDecimal amount;

    @NotNull(message = "Category is required")
    private Long categoryId;

    private Long paymentMethodId;

    @NotNull(message = "Transaction date is required")
    private LocalDate transactionDate;

    @Size(max = 255, message = "Description cannot exceed 255 characters")
    private String description;
}
