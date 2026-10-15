package com.jenitaroselin.personalfinancemanagement.repository;

import com.jenitaroselin.personalfinancemanagement.entity.Category;
import com.jenitaroselin.personalfinancemanagement.enums.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByUser_UserId(Long userId);
    List<Category> findByUser_UserIdAndType(Long userId, CategoryType type);
    Optional<Category> findByCategoryIdAndUser_UserId(Long categoryId, Long userId);
    boolean existsByUser_UserIdAndNameIgnoreCaseAndType(Long userId, String name, CategoryType type);
}
