package com.example.libraryhub.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("CUSTOMER")
@NoArgsConstructor
public class Customer extends User {
    public Customer(Long id, String username, String email, String passwordHash) {
        super(id, username, email, passwordHash, true, java.time.LocalDateTime.now());
    }

    @Override
    public Role getRole() {
        return Role.CUSTOMER;
    }
}