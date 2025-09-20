package com.example.libraryhub.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.Objects;


/**
 * Abstract base user that holds common attributes and behaviors.
 * Lombok generates getters/setters/equals/hashCode/toString.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(of = {"id", "username", "email", "active"})
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "role",
        discriminatorType = DiscriminatorType.STRING, length = 20)
public abstract class User {

    /** Primary key (DB-generated if using JPA). */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Login/display name. */
    @Column(nullable = false, unique = true, length = 50)
    private String username;

    /** Email for contact and login. */
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    /** Store a password hash, NEVER plain text. */
    @Column(nullable = false, length = 200)
    private String passwordHash;

    /** Whether the account is active. */
    private boolean active = true;

    /** Creation timestamp. */
    private LocalDateTime createdAt = LocalDateTime.now();

    /**
     * Polymorphic role exposed by subclasses.
     */
    public abstract Role getRole();

    /**
     * Naive password check for demo.
     * Replace with BCrypt/Argon2 in production.
     */
    public boolean checkPassword(String raw) {
        return Objects.equals(this.passwordHash, hash(raw));
    }

    /** Change password (hashing inside). */
    public void changePassword(String newRaw) {
        this.passwordHash = hash(newRaw);
    }

    /** Deactivate account. */
    public void deactivate() { this.active = false; }

    /** Activate account. */
    public void activate()   { this.active = true;  }

    // --- Demo hash placeholder: replace with a secure implementation. ---
    private String hash(String raw) { return "HASH:" + raw; }
}