package com.jenitaroselin.personalfinancemanagement.dto.response;

import lombok.*;
import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    private Long userId;
    private String fullName;
    private String email;
    private OffsetDateTime createdAt;
}
