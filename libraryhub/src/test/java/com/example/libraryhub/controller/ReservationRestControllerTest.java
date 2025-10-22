package com.example.libraryhub.controller;

import com.example.libraryhub.model.Book;
import com.example.libraryhub.model.Customer;
import com.example.libraryhub.model.Reservation;
import com.example.libraryhub.repository.UserRepository;
import com.example.libraryhub.service.ReservationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReservationRestController.class)
class ReservationRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReservationService reservationService;

    @MockBean
    private UserRepository userRepository;

    private Customer testUser;
    private Book testBook;
    private Reservation testReservation;

    @BeforeEach
    void setUp() {
        testUser = new Customer(1L, "testuser", "test@example.com", "hashedPassword");
        testBook = new Book("Test Book", "Test Author", "Fiction");
        testReservation = new Reservation(testUser, testBook);
    }

    @Test
    @WithMockUser(username = "testuser")
    void testReserveBookSuccess() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(reservationService.createReservation(anyLong(), anyString())).thenReturn(testReservation);

        mockMvc.perform(post("/api/books/1/reserve")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Book reserved successfully!"))
                .andExpect(jsonPath("$.status").value("success"));

        verify(reservationService, times(1)).createReservation(1L, "testuser");
    }

    @Test
    @WithMockUser(username = "testuser")
    void testReserveBookUserNotFound() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/books/1/reserve")
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"));

        verify(reservationService, never()).createReservation(anyLong(), anyString());
    }

    @Test
    @WithMockUser(username = "testuser")
    void testReserveBookNotAvailable() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(reservationService.createReservation(anyLong(), anyString()))
                .thenThrow(new IllegalStateException("Book is not available for reservation."));

        mockMvc.perform(post("/api/books/1/reserve")
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Book is not available for reservation."))
                .andExpect(jsonPath("$.status").value("error"));
    }

    @Test
    void testReserveBookUnauthorized() throws Exception {
        mockMvc.perform(post("/api/books/1/reserve")
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "testuser")
    void testCancelReservationSuccess() throws Exception {
        doNothing().when(reservationService).returnBook(anyLong(), anyString());

        mockMvc.perform(delete("/api/books/1/reserve")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Reservation cancelled successfully!"))
                .andExpect(jsonPath("$.status").value("success"));

        verify(reservationService, times(1)).returnBook(1L, "testuser");
    }

    @Test
    @WithMockUser(username = "testuser")
    void testCancelReservationError() throws Exception {
        doThrow(new RuntimeException("No active reservation"))
                .when(reservationService).returnBook(anyLong(), anyString());

        mockMvc.perform(delete("/api/books/1/reserve")
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("No active reservation"))
                .andExpect(jsonPath("$.status").value("error"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void testReturnBookSuccess() throws Exception {
        doNothing().when(reservationService).returnBook(anyLong(), anyString());

        mockMvc.perform(post("/api/books/1/return")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Book returned successfully!"))
                .andExpect(jsonPath("$.status").value("success"));

        verify(reservationService, times(1)).returnBook(1L, "testuser");
    }

    @Test
    @WithMockUser(username = "testuser")
    void testReturnBookError() throws Exception {
        doThrow(new RuntimeException("You do not hold this reservation."))
                .when(reservationService).returnBook(anyLong(), anyString());

        mockMvc.perform(post("/api/books/1/return")
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("You do not hold this reservation."))
                .andExpect(jsonPath("$.status").value("error"));
    }
}
