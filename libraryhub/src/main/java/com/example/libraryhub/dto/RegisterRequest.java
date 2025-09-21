package com.example.libraryhub.dto;

import lombok.Data;

/** Form data for user registration. */
@Data
public class RegisterRequest {
    private String username;
    private String email;
    private String password;
}
