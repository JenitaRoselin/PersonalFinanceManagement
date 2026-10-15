package com.jenitaroselin.personalfinancemanagement.service;

import com.jenitaroselin.personalfinancemanagement.dto.request.ChangePasswordRequest;
import com.jenitaroselin.personalfinancemanagement.dto.request.LoginRequest;
import com.jenitaroselin.personalfinancemanagement.dto.request.RegisterRequest;
import com.jenitaroselin.personalfinancemanagement.dto.request.UpdateProfileRequest;
import com.jenitaroselin.personalfinancemanagement.dto.response.AuthResponse;
import com.jenitaroselin.personalfinancemanagement.dto.response.UserResponse;
import com.jenitaroselin.personalfinancemanagement.entity.Category;
import com.jenitaroselin.personalfinancemanagement.entity.PaymentMethod;
import com.jenitaroselin.personalfinancemanagement.entity.User;
import com.jenitaroselin.personalfinancemanagement.enums.CategoryType;
import com.jenitaroselin.personalfinancemanagement.exception.BadRequestException;
import com.jenitaroselin.personalfinancemanagement.exception.DuplicateResourceException;
import com.jenitaroselin.personalfinancemanagement.exception.ResourceNotFoundException;
import com.jenitaroselin.personalfinancemanagement.repository.CategoryRepository;
import com.jenitaroselin.personalfinancemanagement.repository.PaymentMethodRepository;
import com.jenitaroselin.personalfinancemanagement.repository.UserRepository;
import com.jenitaroselin.personalfinancemanagement.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

// Manages user registration, credential verification, JWT generation, and default category seeding.
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("An account with this email already exists");
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail().toLowerCase().trim())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .createdAt(OffsetDateTime.now())
                .build();

        user = userRepository.save(user);

        // Auto-seed default categories for this user
        seedDefaultCategories(user);

        // Auto-seed default payment methods for this user
        seedDefaultPaymentMethods(user);

        String token = tokenProvider.generateToken(user.getUserId(), user.getEmail());

        return AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .userId(user.getUserId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail().toLowerCase().trim(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail().toLowerCase().trim())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String token = tokenProvider.generateToken(user.getUserId(), user.getEmail());

        return AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .userId(user.getUserId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .build();
    }

    @Transactional(readOnly = true)
    public UserResponse getCurrentUserProfile(Long userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return UserResponse.builder()
                .userId(user.getUserId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .createdAt(user.getCreatedAt())
                .build();
    }

    @Transactional
    public UserResponse updateProfile(Long userId, UpdateProfileRequest request) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setFullName(request.getFullName());
        user = userRepository.save(user);
        return UserResponse.builder()
                .userId(user.getUserId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .createdAt(user.getCreatedAt())
                .build();
    }

    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPasswordHash())) {
            throw new BadRequestException("Current password does not match");
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Transactional
    public void deleteAccount(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found");
        }
        userRepository.deleteById(userId);
    }

    private void seedDefaultCategories(User user) {
        List<Category> defaultCategories = List.of(
                Category.builder().user(user).name("Food").type(CategoryType.EXPENSE).build(),
                Category.builder().user(user).name("Transport").type(CategoryType.EXPENSE).build(),
                Category.builder().user(user).name("Shopping").type(CategoryType.EXPENSE).build(),
                Category.builder().user(user).name("Entertainment").type(CategoryType.EXPENSE).build(),
                Category.builder().user(user).name("Rent").type(CategoryType.EXPENSE).build(),
                Category.builder().user(user).name("Bills").type(CategoryType.EXPENSE).build(),
                Category.builder().user(user).name("Salary").type(CategoryType.INCOME).build(),
                Category.builder().user(user).name("Freelance").type(CategoryType.INCOME).build(),
                Category.builder().user(user).name("Other Income").type(CategoryType.INCOME).build()
        );
        categoryRepository.saveAll(defaultCategories);
    }

    private void seedDefaultPaymentMethods(User user) {
        List<PaymentMethod> defaultMethods = List.of(
                PaymentMethod.builder().user(user).name("UPI").build(),
                PaymentMethod.builder().user(user).name("Cash").build(),
                PaymentMethod.builder().user(user).name("Card").build(),
                PaymentMethod.builder().user(user).name("Bank").build()
        );
        paymentMethodRepository.saveAll(defaultMethods);
    }
}
