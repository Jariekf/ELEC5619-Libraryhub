package com.example.libraryhub.dto;

import com.example.libraryhub.model.Role;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AdminUpdateUserRequestTest {

    @Test
    void testGettersAndSetters() {
        AdminUpdateUserRequest request = new AdminUpdateUserRequest();
        request.setUsername("updated");
        request.setEmail("updated@test.com");
        request.setPassword("newpass");
        request.setRole(Role.ADMIN);
        request.setActive(true);

        assertEquals("updated", request.getUsername());
        assertEquals("updated@test.com", request.getEmail());
        assertEquals("newpass", request.getPassword());
        assertEquals(Role.ADMIN, request.getRole());
        assertTrue(request.getActive());
    }

    @Test
    void testNullValues() {
        AdminUpdateUserRequest request = new AdminUpdateUserRequest();
        assertNull(request.getUsername());
        assertNull(request.getEmail());
        assertNull(request.getPassword());
        assertNull(request.getRole());
        assertNull(request.getActive());
    }
}
