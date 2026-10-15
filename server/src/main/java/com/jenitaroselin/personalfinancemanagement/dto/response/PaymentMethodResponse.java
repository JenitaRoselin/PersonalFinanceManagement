package com.jenitaroselin.personalfinancemanagement.dto.response;

import lombok.*;
import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentMethodResponse {
    private Long paymentMethodId;
    private String name;
    private OffsetDateTime createdAt;
}
