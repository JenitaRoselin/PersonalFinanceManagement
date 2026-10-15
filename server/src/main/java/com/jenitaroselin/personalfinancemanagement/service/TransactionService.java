package com.jenitaroselin.personalfinancemanagement.service;

import com.jenitaroselin.personalfinancemanagement.dto.request.TransactionRequest;
import com.jenitaroselin.personalfinancemanagement.dto.response.TransactionResponse;
import com.jenitaroselin.personalfinancemanagement.entity.Category;
import com.jenitaroselin.personalfinancemanagement.entity.PaymentMethod;
import com.jenitaroselin.personalfinancemanagement.entity.Transaction;
import com.jenitaroselin.personalfinancemanagement.entity.User;
import com.jenitaroselin.personalfinancemanagement.enums.CategoryType;
import com.jenitaroselin.personalfinancemanagement.enums.TransactionType;
import com.jenitaroselin.personalfinancemanagement.exception.BadRequestException;
import com.jenitaroselin.personalfinancemanagement.exception.ResourceNotFoundException;
import com.jenitaroselin.personalfinancemanagement.repository.CategoryRepository;
import com.jenitaroselin.personalfinancemanagement.repository.PaymentMethodRepository;
import com.jenitaroselin.personalfinancemanagement.repository.TransactionRepository;
import com.jenitaroselin.personalfinancemanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

// Handles business logic, category validation, filtering, sorting, and CRUD operations for transactions.
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final UserRepository userRepository;

    @Transactional
    public TransactionResponse createTransaction(Long userId, TransactionRequest request) {
        validateTransactionRequest(request);

        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Category category = categoryRepository.findByCategoryIdAndUser_UserId(request.getCategoryId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));

        validateCategoryTypeMatch(request.getType(), category);

        PaymentMethod paymentMethod = null;
        if (request.getPaymentMethodId() != null) {
            paymentMethod = paymentMethodRepository.findByPaymentMethodIdAndUser_UserId(request.getPaymentMethodId(), userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Payment method not found with id: " + request.getPaymentMethodId()));
        }

        Transaction transaction = Transaction.builder()
                .user(user)
                .category(category)
                .paymentMethod(paymentMethod)
                .type(request.getType())
                .amount(request.getAmount())
                .transactionDate(request.getTransactionDate())
                .description(request.getDescription())
                .build();

        transaction = transactionRepository.save(transaction);
        return mapToResponse(transaction);
    }

    @Transactional(readOnly = true)
    public Page<TransactionResponse> getTransactions(Long userId, int page, int size, String sortBy, String direction,
                                                     String query, TransactionType type, Long categoryId,
                                                     LocalDate startDate, LocalDate endDate) {
        Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Specification<Transaction> spec = (root, q, cb) -> cb.equal(root.get("user").get("userId"), userId);

        if (query != null && !query.trim().isEmpty()) {
            spec = spec.and((root, q, cb) -> cb.like(cb.lower(root.get("description")), "%" + query.toLowerCase().trim() + "%"));
        }
        if (type != null) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("type"), type));
        }
        if (categoryId != null) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("category").get("categoryId"), categoryId));
        }
        if (startDate != null) {
            spec = spec.and((root, q, cb) -> cb.greaterThanOrEqualTo(root.get("transactionDate"), startDate));
        }
        if (endDate != null) {
            spec = spec.and((root, q, cb) -> cb.lessThanOrEqualTo(root.get("transactionDate"), endDate));
        }

        return transactionRepository.findAll(spec, pageable).map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public TransactionResponse getTransactionById(Long userId, Long transactionId) {
        Transaction transaction = transactionRepository.findByTransactionIdAndUser_UserId(transactionId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + transactionId));
        return mapToResponse(transaction);
    }

    @Transactional
    public TransactionResponse updateTransaction(Long userId, Long transactionId, TransactionRequest request) {
        validateTransactionRequest(request);

        Transaction transaction = transactionRepository.findByTransactionIdAndUser_UserId(transactionId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + transactionId));

        Category category = categoryRepository.findByCategoryIdAndUser_UserId(request.getCategoryId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));

        validateCategoryTypeMatch(request.getType(), category);

        PaymentMethod paymentMethod = null;
        if (request.getPaymentMethodId() != null) {
            paymentMethod = paymentMethodRepository.findByPaymentMethodIdAndUser_UserId(request.getPaymentMethodId(), userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Payment method not found with id: " + request.getPaymentMethodId()));
        }

        transaction.setType(request.getType());
        transaction.setAmount(request.getAmount());
        transaction.setCategory(category);
        transaction.setPaymentMethod(paymentMethod);
        transaction.setTransactionDate(request.getTransactionDate());
        transaction.setDescription(request.getDescription());

        transaction = transactionRepository.save(transaction);
        return mapToResponse(transaction);
    }

    @Transactional
    public void deleteTransaction(Long userId, Long transactionId) {
        Transaction transaction = transactionRepository.findByTransactionIdAndUser_UserId(transactionId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + transactionId));
        transactionRepository.delete(transaction);
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> getTransactionsByDateRange(Long userId, LocalDate startDate, LocalDate endDate) {
        return transactionRepository.findByUser_UserIdAndTransactionDateBetweenOrderByTransactionDateDesc(userId, startDate, endDate)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> getTransactionsByType(Long userId, TransactionType type) {
        return transactionRepository.findByUser_UserIdAndTypeOrderByTransactionDateDesc(userId, type)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> getTransactionsByCategory(Long userId, Long categoryId) {
        return transactionRepository.findByUser_UserIdAndCategory_CategoryIdOrderByTransactionDateDesc(userId, categoryId)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> searchTransactions(Long userId, String query) {
        return transactionRepository.findByUser_UserIdAndDescriptionContainingIgnoreCaseOrderByTransactionDateDesc(userId, query)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> getRecentTransactions(Long userId) {
        return transactionRepository.findTop10ByUser_UserIdOrderByTransactionDateDescCreatedAtDesc(userId)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    private void validateTransactionRequest(TransactionRequest request) {
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Transaction amount must be greater than zero");
        }
        if (request.getTransactionDate() == null) {
            throw new BadRequestException("Transaction date is required");
        }
    }

    private void validateCategoryTypeMatch(TransactionType transactionType, Category category) {
        if (transactionType == TransactionType.INCOME && category.getType() != CategoryType.INCOME) {
            throw new BadRequestException("Category '" + category.getName() + "' is an EXPENSE category and cannot be assigned to an INCOME transaction");
        }
        if (transactionType == TransactionType.EXPENSE && category.getType() != CategoryType.EXPENSE) {
            throw new BadRequestException("Category '" + category.getName() + "' is an INCOME category and cannot be assigned to an EXPENSE transaction");
        }
    }

    private TransactionResponse mapToResponse(Transaction t) {
        return TransactionResponse.builder()
                .transactionId(t.getTransactionId())
                .type(t.getType())
                .amount(t.getAmount())
                .transactionDate(t.getTransactionDate())
                .description(t.getDescription())
                .categoryId(t.getCategory().getCategoryId())
                .categoryName(t.getCategory().getName())
                .paymentMethodId(t.getPaymentMethod() != null ? t.getPaymentMethod().getPaymentMethodId() : null)
                .paymentMethodName(t.getPaymentMethod() != null ? t.getPaymentMethod().getName() : null)
                .createdAt(t.getCreatedAt())
                .updatedAt(t.getUpdatedAt())
                .build();
    }
}
