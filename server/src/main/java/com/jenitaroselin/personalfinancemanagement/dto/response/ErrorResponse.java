package com.jenitaroselin.personalfinancemanagement.dto.response;

import lombok.*;
import java.time.OffsetDateTime;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponse {
    @Builder.Default
    private OffsetDateTime timestamp = OffsetDateTime.now();
    private int status;
    private String error;
    private String message;
    private String path;
    private Map<String, String> fieldErrors;
}
