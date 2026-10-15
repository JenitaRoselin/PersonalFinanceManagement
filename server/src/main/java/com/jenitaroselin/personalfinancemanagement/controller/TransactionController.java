package com.jenitaroselin.personalfinancemanagement.controller;

import com.jenitaroselin.personalfinancemanagement.dto.request.TransactionRequest;
import com.jenitaroselin.personalfinancemanagement.dto.response.TransactionResponse;
import com.jenitaroselin.personalfinancemanagement.enums.TransactionType;
import com.jenitaroselin.personalfinancemanagement.service.TransactionService;
import com.jenitaroselin.personalfinancemanagement.util.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

// Exposes REST endpoints for transaction CRUD, date range queries, type filtering, and text search.
@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(@Valid @RequestBody TransactionRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        TransactionResponse response = transactionService.createTransaction(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<TransactionResponse>> getTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "transactionDate") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction,
            @RequestParam(required = false) String query,
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(transactionService.getTransactions(userId, page, size, sortBy, direction, query, type, categoryId, startDate, endDate));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> getTransactionById(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(transactionService.getTransactionById(userId, id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransactionResponse> updateTransaction(
            @PathVariable Long id,
            @Valid @RequestBody TransactionRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(transactionService.updateTransaction(userId, id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        transactionService.deleteTransaction(userId, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<TransactionResponse>> getTransactionsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(transactionService.getTransactionsByDateRange(userId, startDate, endDate));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<TransactionResponse>> getTransactionsByType(@PathVariable TransactionType type) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(transactionService.getTransactionsByType(userId, type));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<TransactionResponse>> getTransactionsByCategory(@PathVariable Long categoryId) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(transactionService.getTransactionsByCategory(userId, categoryId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<TransactionResponse>> searchTransactions(@RequestParam String query) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(transactionService.searchTransactions(userId, query));
    }
}
