package com.example.libraryhub.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

class UserTest {

    private Customer customer;
    private LocalDateTime testTime;

    @BeforeEach
    void setUp() {
        testTime = LocalDateTime.now();
        customer = new Customer(1L, "testuser", "test@example.com", "hashedPassword");
    }

    @Test
    void testCustomerCreation() {
        assertEquals(1L, customer.getId());
        assertEquals("testuser", customer.getUsername());
        assertEquals("test@example.com", customer.getEmail());
        assertEquals("hashedPassword", customer.getPasswordHash());
        assertTrue(customer.isActive());
        assertNotNull(customer.getCreatedAt());
        assertEquals(Role.CUSTOMER, customer.getRole());
    }

    @Test
    void testDeactivateUser() {
        assertTrue(customer.isActive());
        customer.deactivate();
        assertFalse(customer.isActive());
    }

    @Test
    void testActivateUser() {
        customer.deactivate();
        assertFalse(customer.isActive());
        customer.activate();
        assertTrue(customer.isActive());
    }

    @Test
    void testUserSetters() {
        customer.setUsername("newusername");
        customer.setEmail("newemail@example.com");
        customer.setPasswordHash("newHash");

        assertEquals("newusername", customer.getUsername());
        assertEquals("newemail@example.com", customer.getEmail());
        assertEquals("newHash", customer.getPasswordHash());
    }

    @Test
    void testCustomerRole() {
        assertEquals(Role.CUSTOMER, customer.getRole());
    }

    @Test
    void testReservationsList() {
        assertNotNull(customer.getReservations());
        assertEquals(0, customer.getReservations().size());
    }

    @Test
    void testNoArgsConstructor() {
        Customer emptyCustomer = new Customer();
        assertNotNull(emptyCustomer);
    }
}
