package com.example.libraryhub.service;

import com.example.libraryhub.dto.UserProfileResponse;
import com.example.libraryhub.dto.UserProfileUpdateRequest;
import com.example.libraryhub.model.Customer;
import com.example.libraryhub.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfileServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ProfileService profileService;

    private Customer testUser;

    @BeforeEach
    void setUp() {
        testUser = new Customer(1L, "testuser", "test@example.com", "hashedPassword");
    }

    @Test
    void testGetProfile() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        UserProfileResponse response = profileService.getProfile("testuser");

        assertNotNull(response);
        assertEquals("testuser", response.getUsername());
        assertEquals("test@example.com", response.getEmail());
        assertEquals(1L, response.getId());
        verify(userRepository, times(1)).findByUsername("testuser");
    }

    @Test
    void testGetProfileUserNotFound() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> profileService.getProfile("nonexistent")
        );

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void testUpdateProfileUsername() {
        UserProfileUpdateRequest request = new UserProfileUpdateRequest();
        request.setUsername("newusername");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(userRepository.existsByUsername("newusername")).thenReturn(false);
        when(userRepository.save(any())).thenReturn(testUser);

        UserProfileResponse response = profileService.updateProfile("testuser", request);

        assertNotNull(response);
        verify(userRepository, times(1)).existsByUsername("newusername");
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void testUpdateProfileUsernameAlreadyExists() {
        UserProfileUpdateRequest request = new UserProfileUpdateRequest();
        request.setUsername("existinguser");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(userRepository.existsByUsername("existinguser")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> profileService.updateProfile("testuser", request)
        );

        assertEquals("Username already exists", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void testUpdateProfileEmail() {
        UserProfileUpdateRequest request = new UserProfileUpdateRequest();
        request.setEmail("newemail@example.com");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail("newemail@example.com")).thenReturn(false);
        when(userRepository.save(any())).thenReturn(testUser);

        UserProfileResponse response = profileService.updateProfile("testuser", request);

        assertNotNull(response);
        verify(userRepository, times(1)).existsByEmail("newemail@example.com");
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void testUpdateProfileEmailAlreadyExists() {
        UserProfileUpdateRequest request = new UserProfileUpdateRequest();
        request.setEmail("existing@example.com");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> profileService.updateProfile("testuser", request)
        );

        assertEquals("Email already exists", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void testUpdateProfilePassword() {
        UserProfileUpdateRequest request = new UserProfileUpdateRequest();
        request.setPassword("newpassword");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode("newpassword")).thenReturn("newHashedPassword");
        when(userRepository.save(any())).thenReturn(testUser);

        UserProfileResponse response = profileService.updateProfile("testuser", request);

        assertNotNull(response);
        verify(passwordEncoder, times(1)).encode("newpassword");
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void testUpdateProfileSameUsername() {
        UserProfileUpdateRequest request = new UserProfileUpdateRequest();
        request.setUsername("testuser"); // Same as current

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(userRepository.save(any())).thenReturn(testUser);

        UserProfileResponse response = profileService.updateProfile("testuser", request);

        assertNotNull(response);
        verify(userRepository, never()).existsByUsername(anyString());
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void testUpdateProfileBlankValues() {
        UserProfileUpdateRequest request = new UserProfileUpdateRequest();
        request.setUsername("   "); // Blank
        request.setEmail(""); // Empty

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(userRepository.save(any())).thenReturn(testUser);

        UserProfileResponse response = profileService.updateProfile("testuser", request);

        assertNotNull(response);
        // Should not check existence for blank values
        verify(userRepository, never()).existsByUsername(anyString());
        verify(userRepository, never()).existsByEmail(anyString());
    }
}
