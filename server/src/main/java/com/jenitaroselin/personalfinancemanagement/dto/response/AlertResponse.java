package com.jenitaroselin.personalfinancemanagement.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlertResponse {
    private String type; // INFO, WARNING, DANGER
    private String title;
    private String message;
    private String categoryName;
    private Double thresholdPercentage;
}
