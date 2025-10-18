package com.example.libraryhub.dto;

import com.example.libraryhub.model.Role;
import lombok.Data;

/**
 * Payload for admin-managed user updates.
 */
@Data
public class AdminUpdateUserRequest {
    private String username;
    private String email;
    private String password;
    private Role role;
    private Boolean active;
}

