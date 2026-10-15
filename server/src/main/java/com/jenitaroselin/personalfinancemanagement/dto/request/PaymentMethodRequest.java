package com.jenitaroselin.personalfinancemanagement.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentMethodRequest {

    @NotBlank(message = "Payment method name is required")
    @Size(min = 1, max = 50, message = "Name must be between 1 and 50 characters")
    private String name;
}
