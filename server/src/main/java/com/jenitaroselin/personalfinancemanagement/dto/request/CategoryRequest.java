package com.jenitaroselin.personalfinancemanagement.dto.request;

import com.jenitaroselin.personalfinancemanagement.enums.CategoryType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryRequest {

    @NotBlank(message = "Category name is required")
    @Size(min = 1, max = 60, message = "Name must be between 1 and 60 characters")
    private String name;

    @NotNull(message = "Category type is required (INCOME or EXPENSE)")
    private CategoryType type;
}
