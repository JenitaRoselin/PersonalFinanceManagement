package com.jenitaroselin.personalfinancemanagement.entity;

import com.jenitaroselin.personalfinancemanagement.enums.CategoryType;
import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;

// Represents a user-owned income or expense category used to classify transactions.
@Entity
@Table(name = "categories", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "name", "type"}),
    @UniqueConstraint(columnNames = {"category_id", "user_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long categoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 60)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private CategoryType type;

    @Column(name = "created_at", nullable = false)
    @Builder.Default
    private OffsetDateTime createdAt = OffsetDateTime.now();
}
