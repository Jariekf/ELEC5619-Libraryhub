package com.example.libraryhub.service;

import com.example.libraryhub.dto.RegisterRequest;
import com.example.libraryhub.model.Customer;
import com.example.libraryhub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/** Registration use cases for the application. */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Register a new customer. Basic duplicate checks only.
     * @throws IllegalArgumentException when username/email already exists
     */
    public void registerCustomer(RegisterRequest req) {
        if (userRepository.existsByUsername(req.getUsername())) {
            throw new IllegalArgumentException("Username already taken");
        }
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new IllegalArgumentException("Email already used");
        }
        // Hash the password
        String hash = passwordEncoder.encode(req.getPassword());

        // Create a Customer entity (active by default)
        Customer c = new Customer(
                null,
                req.getUsername(),
                req.getEmail(),
                hash
        );
        userRepository.save(c);
    }
}
