package com.example.libraryhub.controller;

import com.example.libraryhub.dto.BookDTO;
import com.example.libraryhub.model.Book;
import com.example.libraryhub.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookRestController.class)
class BookRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @Autowired
    private ObjectMapper objectMapper;

    private Book testBook;

    @BeforeEach
    void setUp() {
        testBook = new Book("Test Book", "Test Author", "Fiction");
        testBook.setAvailable(true);
        testBook.setReviews(Collections.emptyList());
    }

    @Test
    @WithMockUser
    void testGetAllBooks() throws Exception {
        when(bookService.getAllBooks()).thenReturn(Arrays.asList(testBook));

        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Book"))
                .andExpect(jsonPath("$[0].author").value("Test Author"));

        verify(bookService, times(1)).getAllBooks();
    }

    @Test
    @WithMockUser
    void testGetBookById() throws Exception {
        when(bookService.getBookById(1L)).thenReturn(testBook);

        mockMvc.perform(get("/api/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Book"));

        verify(bookService, times(1)).getBookById(1L);
    }

    @Test
    @WithMockUser
    void testGetBookByIdNotFound() throws Exception {
        when(bookService.getBookById(999L)).thenReturn(null);

        mockMvc.perform(get("/api/books/999"))
                .andExpect(status().isNotFound());

        verify(bookService, times(1)).getBookById(999L);
    }

    @Test
    @WithMockUser
    void testSearchBooks() throws Exception {
        when(bookService.searchByTitle("Test")).thenReturn(Arrays.asList(testBook));

        mockMvc.perform(post("/api/books/search")
                        .param("title", "Test")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Book"));

        verify(bookService, times(1)).searchByTitle("Test");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testAddBook() throws Exception {
        when(bookService.saveBook(any(Book.class))).thenReturn(testBook);

        mockMvc.perform(post("/api/books/add")
                        .param("title", "Test Book")
                        .param("author", "Test Author")
                        .param("genre", "Fiction")
                        .param("available", "true")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Book"));

        verify(bookService, times(1)).saveBook(any(Book.class));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void testAddBookForbidden() throws Exception {
        mockMvc.perform(post("/api/books/add")
                        .param("title", "Test Book")
                        .param("author", "Test Author")
                        .param("genre", "Fiction")
                        .with(csrf()))
                .andExpect(status().isForbidden());

        verify(bookService, never()).saveBook(any(Book.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateBook() throws Exception {
        when(bookService.getBookById(1L)).thenReturn(testBook);
        when(bookService.saveBook(any(Book.class))).thenReturn(testBook);

        mockMvc.perform(post("/api/books/1/edit")
                        .param("title", "Updated Title")
                        .param("author", "Updated Author")
                        .param("genre", "Drama")
                        .param("available", "false")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"));

        verify(bookService, times(1)).getBookById(1L);
        verify(bookService, times(1)).saveBook(any(Book.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateBookNotFound() throws Exception {
        when(bookService.getBookById(999L)).thenReturn(null);

        mockMvc.perform(post("/api/books/999/edit")
                        .param("title", "Updated Title")
                        .param("author", "Updated Author")
                        .param("genre", "Drama")
                        .param("available", "false")
                        .with(csrf()))
                .andExpect(status().isNotFound());

        verify(bookService, never()).saveBook(any(Book.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteBook() throws Exception {
        when(bookService.getBookById(1L)).thenReturn(testBook);
        doNothing().when(bookService).deleteBook(1L);

        mockMvc.perform(post("/api/books/1/delete")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Book deleted successfully"))
                .andExpect(jsonPath("$.status").value("success"));

        verify(bookService, times(1)).deleteBook(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteBookNotFound() throws Exception {
        when(bookService.getBookById(999L)).thenReturn(null);

        mockMvc.perform(post("/api/books/999/delete")
                        .with(csrf()))
                .andExpect(status().isNotFound());

        verify(bookService, never()).deleteBook(anyLong());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteBookRestful() throws Exception {
        when(bookService.getBookById(1L)).thenReturn(testBook);
        doNothing().when(bookService).deleteBook(1L);

        mockMvc.perform(delete("/api/books/1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Book deleted successfully"));

        verify(bookService, times(1)).deleteBook(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteBookError() throws Exception {
        when(bookService.getBookById(1L)).thenReturn(testBook);
        doThrow(new RuntimeException("Database error")).when(bookService).deleteBook(1L);

        mockMvc.perform(post("/api/books/1/delete")
                        .with(csrf()))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Failed to delete book"));
    }
}
