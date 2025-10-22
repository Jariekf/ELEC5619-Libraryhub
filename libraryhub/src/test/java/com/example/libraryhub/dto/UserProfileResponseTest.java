package com.example.libraryhub.dto;

import com.example.libraryhub.model.Role;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class UserProfileResponseTest {

    @Test
    void testAllArgsConstructor() {
        LocalDateTime createdAt = LocalDateTime.of(2024, 1, 1, 10, 0);
        
        UserProfileResponse response = new UserProfileResponse(
            1L,
            "testuser",
            "test@example.com",
            Role.CUSTOMER,
            true,
            createdAt
        );
        
        assertEquals(1L, response.getId());
        assertEquals("testuser", response.getUsername());
        assertEquals("test@example.com", response.getEmail());
        assertEquals(Role.CUSTOMER, response.getRole());
        assertTrue(response.isActive());
        assertEquals(createdAt, response.getCreatedAt());
    }

    @Test
    void testGettersAndSetters() {
        LocalDateTime createdAt = LocalDateTime.now();
        
        UserProfileResponse response = new UserProfileResponse(
            1L, "user", "user@test.com", Role.CUSTOMER, true, createdAt
        );
        
        response.setId(2L);
        response.setUsername("newuser");
        response.setEmail("new@test.com");
        response.setRole(Role.ADMIN);
        response.setActive(false);
        LocalDateTime newDate = LocalDateTime.of(2024, 2, 1, 12, 0);
        response.setCreatedAt(newDate);

        assertEquals(2L, response.getId());
        assertEquals("newuser", response.getUsername());
        assertEquals("new@test.com", response.getEmail());
        assertEquals(Role.ADMIN, response.getRole());
        assertFalse(response.isActive());
        assertEquals(newDate, response.getCreatedAt());
    }

    @Test
    void testWithCustomerRole() {
        LocalDateTime createdAt = LocalDateTime.of(2024, 1, 1, 10, 0);
        
        UserProfileResponse response = new UserProfileResponse(
            1L, "customer", "customer@test.com", Role.CUSTOMER, true, createdAt
        );
        
        assertEquals(Role.CUSTOMER, response.getRole());
    }

    @Test
    void testWithAdminRole() {
        LocalDateTime createdAt = LocalDateTime.of(2024, 1, 1, 10, 0);
        
        UserProfileResponse response = new UserProfileResponse(
            2L, "admin", "admin@test.com", Role.ADMIN, true, createdAt
        );
        
        assertEquals(Role.ADMIN, response.getRole());
    }

    @Test
    void testInactiveUser() {
        LocalDateTime createdAt = LocalDateTime.of(2024, 1, 1, 10, 0);
        
        UserProfileResponse response = new UserProfileResponse(
            3L, "inactive", "inactive@test.com", Role.CUSTOMER, false, createdAt
        );
        
        assertFalse(response.isActive());
    }

    @Test
    void testActiveUser() {
        LocalDateTime createdAt = LocalDateTime.of(2024, 1, 1, 10, 0);
        
        UserProfileResponse response = new UserProfileResponse(
            4L, "active", "active@test.com", Role.CUSTOMER, true, createdAt
        );
        
        assertTrue(response.isActive());
    }

    @Test
    void testCreatedAtPreserved() {
        LocalDateTime exactDate = LocalDateTime.of(2024, 6, 15, 14, 30, 45);
        
        UserProfileResponse response = new UserProfileResponse(
            5L, "user", "user@test.com", Role.CUSTOMER, true, exactDate
        );
        
        assertEquals(exactDate, response.getCreatedAt());
    }

    @Test
    void testMultipleInstancesIndependent() {
        LocalDateTime date1 = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime date2 = LocalDateTime.of(2024, 2, 1, 12, 0);
        
        UserProfileResponse response1 = new UserProfileResponse(
            1L, "user1", "user1@test.com", Role.CUSTOMER, true, date1
        );
        
        UserProfileResponse response2 = new UserProfileResponse(
            2L, "user2", "user2@test.com", Role.ADMIN, false, date2
        );
        
        assertEquals(1L, response1.getId());
        assertEquals(2L, response2.getId());
        assertEquals("user1", response1.getUsername());
        assertEquals("user2", response2.getUsername());
        assertEquals(Role.CUSTOMER, response1.getRole());
        assertEquals(Role.ADMIN, response2.getRole());
        assertTrue(response1.isActive());
        assertFalse(response2.isActive());
    }
}
