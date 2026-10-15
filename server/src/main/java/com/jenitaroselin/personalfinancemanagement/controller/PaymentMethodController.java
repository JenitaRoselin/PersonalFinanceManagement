package com.jenitaroselin.personalfinancemanagement.controller;

import com.jenitaroselin.personalfinancemanagement.dto.request.PaymentMethodRequest;
import com.jenitaroselin.personalfinancemanagement.dto.response.PaymentMethodResponse;
import com.jenitaroselin.personalfinancemanagement.service.PaymentMethodService;
import com.jenitaroselin.personalfinancemanagement.util.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Exposes REST endpoints for creating, retrieving, updating, and deleting user payment methods.
@RestController
@RequestMapping("/api/payment-methods")
@RequiredArgsConstructor
public class PaymentMethodController {

    private final PaymentMethodService paymentMethodService;

    @PostMapping
    public ResponseEntity<PaymentMethodResponse> createPaymentMethod(@Valid @RequestBody PaymentMethodRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        PaymentMethodResponse response = paymentMethodService.createPaymentMethod(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<PaymentMethodResponse>> getAllPaymentMethods() {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(paymentMethodService.getAllPaymentMethods(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentMethodResponse> getPaymentMethodById(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(paymentMethodService.getPaymentMethodById(userId, id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PaymentMethodResponse> updatePaymentMethod(
            @PathVariable Long id,
            @Valid @RequestBody PaymentMethodRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(paymentMethodService.updatePaymentMethod(userId, id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePaymentMethod(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        paymentMethodService.deletePaymentMethod(userId, id);
        return ResponseEntity.noContent().build();
    }
}
