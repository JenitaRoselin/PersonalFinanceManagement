package com.jenitaroselin.personalfinancemanagement.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;

// Represents a user's payment method such as Cash, UPI, Debit Card, or Credit Card.
@Entity
@Table(name = "payment_methods", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "name"}),
    @UniqueConstraint(columnNames = {"payment_method_id", "user_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentMethod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_method_id")
    private Long paymentMethodId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(name = "created_at", nullable = false)
    @Builder.Default
    private OffsetDateTime createdAt = OffsetDateTime.now();
}
