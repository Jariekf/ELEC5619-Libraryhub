package com.example.libraryhub.service;

import com.example.libraryhub.dto.AdminCreateUserRequest;
import com.example.libraryhub.dto.AdminUpdateUserRequest;
import com.example.libraryhub.dto.AdminUserResponse;
import com.example.libraryhub.model.Admin;
import com.example.libraryhub.model.Customer;
import com.example.libraryhub.model.Role;
import com.example.libraryhub.model.User;
import com.example.libraryhub.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Administrative use cases for managing user accounts (FR-19 to FR-21).
 */
@Service
@RequiredArgsConstructor
public class AdminAccountService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional(readOnly = true)
    public List<AdminUserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public AdminUserResponse createUser(AdminCreateUserRequest request) {
        String username = requireText(request.getUsername(), "Username is required");
        String email = requireText(request.getEmail(), "Email is required");
        String password = requireText(request.getPassword(), "Password is required");
        Role role = request.getRole() != null ? request.getRole() : Role.CUSTOMER;

        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists");
        }

        String hash = passwordEncoder.encode(password);
        User user = role == Role.ADMIN
                ? new Admin(null, username, email, hash)
                : new Customer(null, username, email, hash);
        user.setActive(request.isActive());

        User saved = userRepository.save(user);
        return toResponse(saved);
    }

    @Transactional
    public AdminUserResponse updateUser(Long id, AdminUpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (request.getUsername() != null) {
            String username = requireText(request.getUsername(), "Username cannot be blank");
            if (!username.equalsIgnoreCase(user.getUsername()) && userRepository.existsByUsername(username)) {
                throw new IllegalArgumentException("Username already exists");
            }
            user.setUsername(username);
        }

        if (request.getEmail() != null) {
            String email = requireText(request.getEmail(), "Email cannot be blank");
            if (!email.equalsIgnoreCase(user.getEmail()) && userRepository.existsByEmail(email)) {
                throw new IllegalArgumentException("Email already exists");
            }
            user.setEmail(email);
        }

        if (request.getActive() != null) {
            user.setActive(request.getActive());
        }

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        }

        Role requestedRole = request.getRole();
        boolean roleChanged = requestedRole != null && !Objects.equals(requestedRole, user.getRole());

        userRepository.save(user);
        userRepository.flush();

        if (roleChanged) {
            userRepository.updateUserRole(user.getId(), requestedRole.name());
            userRepository.flush();
            entityManager.clear();
            user = userRepository.findById(user.getId())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
        }

        return toResponse(user);
    }

    @Transactional
    public void deleteUser(Long id, String actingUsername) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (user.getUsername().equalsIgnoreCase(actingUsername)) {
            throw new IllegalArgumentException("You cannot delete your own account");
        }

        userRepository.delete(user);
    }

    private AdminUserResponse toResponse(User user) {
        return new AdminUserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.isActive(),
                user.getCreatedAt()
        );
    }

    private String requireText(String value, String message) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(message);
        }
        return value.trim();
    }
}

