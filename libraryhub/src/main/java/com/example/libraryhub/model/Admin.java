package com.example.libraryhub.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * Library administrator: manages books and users.
 */
@Getter
@NoArgsConstructor
@ToString(callSuper = true)
@Entity
@DiscriminatorValue("ADMIN")
public class Admin extends User {

    /** Simple audit log for admin operations (demo only). */
    private final List<String> auditLogs = new ArrayList<>();

    public Admin(Long id, String username, String email, String passwordHash) {
        super(id, username, email, passwordHash, true, java.time.LocalDateTime.now());
    }

    @Override
    public Role getRole() { return Role.ADMIN; }

    // --- Management actions (wire real persistence in services) ---
    public void addBook(String bookId)    { auditLogs.add("ADD_BOOK:" + bookId); }
    public void updateBook(String bookId) { auditLogs.add("UPDATE_BOOK:" + bookId); }
    public void removeBook(String bookId) { auditLogs.add("REMOVE_BOOK:" + bookId); }

    public void addUser(User user)        { auditLogs.add("ADD_USER:" + user.getUsername()); }
    public void updateUser(User user)     { auditLogs.add("UPDATE_USER:" + user.getUsername()); }
    public void deleteUser(User user)     { auditLogs.add("DELETE_USER:" + user.getUsername()); }
}