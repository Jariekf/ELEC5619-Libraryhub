package com.example.libraryhub.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RegisterRequestTest {

    @Test
    void testSettersAndGetters() {
        RegisterRequest request = new RegisterRequest();
        
        request.setUsername("testuser");
        request.setEmail("test@example.com");
        request.setPassword("password123");

        assertEquals("testuser", request.getUsername());
        assertEquals("test@example.com", request.getEmail());
        assertEquals("password123", request.getPassword());
    }

    @Test
    void testDefaultConstructor() {
        RegisterRequest request = new RegisterRequest();
        assertNotNull(request);
    }
}
