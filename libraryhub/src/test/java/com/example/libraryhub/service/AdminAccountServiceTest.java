package com.example.libraryhub.service;

import com.example.libraryhub.dto.AdminCreateUserRequest;
import com.example.libraryhub.dto.AdminUpdateUserRequest;
import com.example.libraryhub.dto.AdminUserResponse;
import com.example.libraryhub.model.Admin;
import com.example.libraryhub.model.Customer;
import com.example.libraryhub.model.Role;
import com.example.libraryhub.model.User;
import com.example.libraryhub.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminAccountServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private AdminAccountService adminAccountService;

    private User customer;
    private User admin;

    @BeforeEach
    void setUp() {
        customer = new Customer(1L, "customer1", "customer@test.com", "hash1");
        admin = new Admin(2L, "admin1", "admin@test.com", "hash2");
    }

    @Test
    void testGetAllUsers() {
        when(userRepository.findAll()).thenReturn(Arrays.asList(customer, admin));

        List<AdminUserResponse> users = adminAccountService.getAllUsers();

        assertEquals(2, users.size());
        assertEquals("customer1", users.get(0).getUsername());
        assertEquals("admin1", users.get(1).getUsername());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testCreateUserCustomer() {
        AdminCreateUserRequest request = new AdminCreateUserRequest();
        request.setUsername("newuser");
        request.setEmail("new@test.com");
        request.setPassword("password");
        request.setRole(Role.CUSTOMER);
        request.setActive(true);

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("new@test.com")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenReturn(customer);

        AdminUserResponse response = adminAccountService.createUser(request);

        assertNotNull(response);
        verify(passwordEncoder, times(1)).encode("password");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testCreateUserAdmin() {
        AdminCreateUserRequest request = new AdminCreateUserRequest();
        request.setUsername("newadmin");
        request.setEmail("newadmin@test.com");
        request.setPassword("password");
        request.setRole(Role.ADMIN);

        when(userRepository.existsByUsername("newadmin")).thenReturn(false);
        when(userRepository.existsByEmail("newadmin@test.com")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenReturn(admin);

        AdminUserResponse response = adminAccountService.createUser(request);

        assertNotNull(response);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testCreateUserDuplicateUsername() {
        AdminCreateUserRequest request = new AdminCreateUserRequest();
        request.setUsername("duplicate");
        request.setEmail("new@test.com");
        request.setPassword("password");

        when(userRepository.existsByUsername("duplicate")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> adminAccountService.createUser(request));
    }

    @Test
    void testCreateUserDuplicateEmail() {
        AdminCreateUserRequest request = new AdminCreateUserRequest();
        request.setUsername("newuser");
        request.setEmail("duplicate@test.com");
        request.setPassword("password");

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("duplicate@test.com")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> adminAccountService.createUser(request));
    }

    @Test
    void testCreateUserMissingUsername() {
        AdminCreateUserRequest request = new AdminCreateUserRequest();
        request.setEmail("new@test.com");
        request.setPassword("password");

        assertThrows(IllegalArgumentException.class, () -> adminAccountService.createUser(request));
    }

    @Test
    void testUpdateUserUsername() {
        AdminUpdateUserRequest request = new AdminUpdateUserRequest();
        request.setUsername("updated");

        when(userRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(userRepository.existsByUsername("updated")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(customer);

        AdminUserResponse response = adminAccountService.updateUser(1L, request);

        assertNotNull(response);
        verify(userRepository, times(1)).save(customer);
    }

    @Test
    void testUpdateUserEmail() {
        AdminUpdateUserRequest request = new AdminUpdateUserRequest();
        request.setEmail("updated@test.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(userRepository.existsByEmail("updated@test.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(customer);

        AdminUserResponse response = adminAccountService.updateUser(1L, request);

        assertNotNull(response);
        verify(userRepository, times(1)).save(customer);
    }

    @Test
    void testUpdateUserPassword() {
        AdminUpdateUserRequest request = new AdminUpdateUserRequest();
        request.setPassword("newpassword");

        when(userRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(passwordEncoder.encode("newpassword")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenReturn(customer);

        adminAccountService.updateUser(1L, request);

        verify(passwordEncoder, times(1)).encode("newpassword");
    }

    @Test
    void testUpdateUserActive() {
        AdminUpdateUserRequest request = new AdminUpdateUserRequest();
        request.setActive(false);

        when(userRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(userRepository.save(any(User.class))).thenReturn(customer);

        adminAccountService.updateUser(1L, request);

        verify(userRepository, times(1)).save(customer);
    }

    @Test
    void testUpdateUserNotFound() {
        AdminUpdateUserRequest request = new AdminUpdateUserRequest();
        request.setUsername("updated");

        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, 
            () -> adminAccountService.updateUser(999L, request));
    }

    @Test
    void testDeleteUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(customer));

        adminAccountService.deleteUser(1L, "admin1");

        verify(userRepository, times(1)).delete(customer);
    }

    @Test
    void testDeleteUserSelfDeletion() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(customer));

        assertThrows(IllegalArgumentException.class, 
            () -> adminAccountService.deleteUser(1L, "customer1"));
    }

    @Test
    void testDeleteUserNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, 
            () -> adminAccountService.deleteUser(999L, "admin1"));
    }
}
