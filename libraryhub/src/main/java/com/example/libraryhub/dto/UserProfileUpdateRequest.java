package com.example.libraryhub.dto;

import lombok.Data;

/**
 * Payload submitted by an authenticated user when updating their profile.
 */
@Data
public class UserProfileUpdateRequest {
    private String username;
    private String email;
    private String password;
}

