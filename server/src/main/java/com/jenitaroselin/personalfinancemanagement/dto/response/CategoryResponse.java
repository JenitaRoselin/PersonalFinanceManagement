package com.jenitaroselin.personalfinancemanagement.dto.response;

import com.jenitaroselin.personalfinancemanagement.enums.CategoryType;
import lombok.*;
import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryResponse {
    private Long categoryId;
    private String name;
    private CategoryType type;
    private OffsetDateTime createdAt;
}
