package com.example.libraryhub.dto;

import com.example.libraryhub.model.Role;
import lombok.Data;

/**
 * Payload when an admin creates a new user.
 */
@Data
public class AdminCreateUserRequest {
    private String username;
    private String email;
    private String password;
    private Role role = Role.CUSTOMER;
    private boolean active = true;
}

