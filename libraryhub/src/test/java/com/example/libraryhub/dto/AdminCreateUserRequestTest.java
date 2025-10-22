package com.example.libraryhub.dto;

import com.example.libraryhub.model.Role;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AdminCreateUserRequestTest {

    @Test
    void testGettersAndSetters() {
        AdminCreateUserRequest request = new AdminCreateUserRequest();
        request.setUsername("testuser");
        request.setEmail("test@test.com");
        request.setPassword("password");
        request.setRole(Role.CUSTOMER);
        request.setActive(true);

        assertEquals("testuser", request.getUsername());
        assertEquals("test@test.com", request.getEmail());
        assertEquals("password", request.getPassword());
        assertEquals(Role.CUSTOMER, request.getRole());
        assertTrue(request.isActive());
    }

    @Test
    void testDefaultActiveIsTrue() {
        AdminCreateUserRequest request = new AdminCreateUserRequest();
        // Default is true for active field
        assertTrue(request.isActive());
    }
}
