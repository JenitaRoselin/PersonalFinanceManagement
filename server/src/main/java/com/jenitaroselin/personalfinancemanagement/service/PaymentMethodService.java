package com.jenitaroselin.personalfinancemanagement.service;

import com.jenitaroselin.personalfinancemanagement.dto.request.PaymentMethodRequest;
import com.jenitaroselin.personalfinancemanagement.dto.response.PaymentMethodResponse;
import com.jenitaroselin.personalfinancemanagement.entity.PaymentMethod;
import com.jenitaroselin.personalfinancemanagement.entity.User;
import com.jenitaroselin.personalfinancemanagement.exception.BadRequestException;
import com.jenitaroselin.personalfinancemanagement.exception.DuplicateResourceException;
import com.jenitaroselin.personalfinancemanagement.exception.ResourceNotFoundException;
import com.jenitaroselin.personalfinancemanagement.repository.PaymentMethodRepository;
import com.jenitaroselin.personalfinancemanagement.repository.TransactionRepository;
import com.jenitaroselin.personalfinancemanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

// Manages user payment methods creation, listing, updating, and deletion.
@Service
@RequiredArgsConstructor
public class PaymentMethodService {

    private final PaymentMethodRepository paymentMethodRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    public PaymentMethodResponse createPaymentMethod(Long userId, PaymentMethodRequest request) {
        if (paymentMethodRepository.existsByUser_UserIdAndNameIgnoreCase(userId, request.getName().trim())) {
            throw new DuplicateResourceException("Payment method with this name already exists");
        }

        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        PaymentMethod paymentMethod = PaymentMethod.builder()
                .user(user)
                .name(request.getName().trim())
                .build();

        paymentMethod = paymentMethodRepository.save(paymentMethod);
        return mapToResponse(paymentMethod);
    }

    @Transactional(readOnly = true)
    public List<PaymentMethodResponse> getAllPaymentMethods(Long userId) {
        return paymentMethodRepository.findByUser_UserId(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PaymentMethodResponse getPaymentMethodById(Long userId, Long paymentMethodId) {
        PaymentMethod paymentMethod = paymentMethodRepository.findByPaymentMethodIdAndUser_UserId(paymentMethodId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment method not found with id: " + paymentMethodId));
        return mapToResponse(paymentMethod);
    }

    @Transactional
    public PaymentMethodResponse updatePaymentMethod(Long userId, Long paymentMethodId, PaymentMethodRequest request) {
        PaymentMethod paymentMethod = paymentMethodRepository.findByPaymentMethodIdAndUser_UserId(paymentMethodId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment method not found with id: " + paymentMethodId));

        if (!paymentMethod.getName().equalsIgnoreCase(request.getName().trim())) {
            if (paymentMethodRepository.existsByUser_UserIdAndNameIgnoreCase(userId, request.getName().trim())) {
                throw new DuplicateResourceException("Another payment method with this name already exists");
            }
        }

        paymentMethod.setName(request.getName().trim());
        paymentMethod = paymentMethodRepository.save(paymentMethod);
        return mapToResponse(paymentMethod);
    }

    @Transactional
    public void deletePaymentMethod(Long userId, Long paymentMethodId) {
        PaymentMethod paymentMethod = paymentMethodRepository.findByPaymentMethodIdAndUser_UserId(paymentMethodId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment method not found with id: " + paymentMethodId));

        if (transactionRepository.existsByPaymentMethod_PaymentMethodId(paymentMethodId)) {
            throw new BadRequestException("Cannot delete payment method '" + paymentMethod.getName() + "' because it is linked to transactions.");
        }

        paymentMethodRepository.delete(paymentMethod);
    }

    private PaymentMethodResponse mapToResponse(PaymentMethod paymentMethod) {
        return PaymentMethodResponse.builder()
                .paymentMethodId(paymentMethod.getPaymentMethodId())
                .name(paymentMethod.getName())
                .createdAt(paymentMethod.getCreatedAt())
                .build();
    }
}
