package com.jenitaroselin.personalfinancemanagement.repository;

import com.jenitaroselin.personalfinancemanagement.entity.Transaction;
import com.jenitaroselin.personalfinancemanagement.enums.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long>, JpaSpecificationExecutor<Transaction> {

    Page<Transaction> findByUser_UserId(Long userId, Pageable pageable);

    Optional<Transaction> findByTransactionIdAndUser_UserId(Long transactionId, Long userId);

    List<Transaction> findByUser_UserIdAndTransactionDateBetweenOrderByTransactionDateDesc(
            Long userId, LocalDate startDate, LocalDate endDate);

    List<Transaction> findByUser_UserIdAndTypeOrderByTransactionDateDesc(
            Long userId, TransactionType type);

    List<Transaction> findByUser_UserIdAndCategory_CategoryIdOrderByTransactionDateDesc(
            Long userId, Long categoryId);

    List<Transaction> findByUser_UserIdAndDescriptionContainingIgnoreCaseOrderByTransactionDateDesc(
            Long userId, String description);

    List<Transaction> findTop10ByUser_UserIdOrderByTransactionDateDescCreatedAtDesc(Long userId);

    boolean existsByCategory_CategoryId(Long categoryId);

    boolean existsByPaymentMethod_PaymentMethodId(Long paymentMethodId);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
           "WHERE t.user.userId = :userId " +
           "AND t.category.categoryId = :categoryId " +
           "AND t.type = 'EXPENSE' " +
           "AND t.transactionDate >= :startDate AND t.transactionDate <= :endDate")
    BigDecimal sumExpensesByUserAndCategoryAndDateRange(
            @Param("userId") Long userId,
            @Param("categoryId") Long categoryId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
           "WHERE t.user.userId = :userId AND t.type = :type")
    BigDecimal sumAmountByUserAndType(
            @Param("userId") Long userId,
            @Param("type") TransactionType type);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
           "WHERE t.user.userId = :userId AND t.type = :type " +
           "AND t.transactionDate >= :startDate AND t.transactionDate <= :endDate")
    BigDecimal sumAmountByUserAndTypeAndDateRange(
            @Param("userId") Long userId,
            @Param("type") TransactionType type,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT t.category.categoryId, t.category.name, SUM(t.amount) " +
           "FROM Transaction t " +
           "WHERE t.user.userId = :userId AND t.type = 'EXPENSE' " +
           "AND t.transactionDate >= :startDate AND t.transactionDate <= :endDate " +
           "GROUP BY t.category.categoryId, t.category.name " +
           "ORDER BY SUM(t.amount) DESC")
    List<Object[]> sumSpendingByCategory(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT t FROM Transaction t WHERE t.user.userId = :userId AND t.type = 'EXPENSE' ORDER BY t.amount DESC")
    List<Transaction> findTopExpensesByUser(@Param("userId") Long userId, Pageable pageable);
}
