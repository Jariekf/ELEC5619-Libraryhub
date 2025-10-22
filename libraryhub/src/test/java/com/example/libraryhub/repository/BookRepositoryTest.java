package com.example.libraryhub.repository;

import com.example.libraryhub.model.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BookRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BookRepository bookRepository;

    private Book testBook1;
    private Book testBook2;

    @BeforeEach
    void setUp() {
        testBook1 = new Book("Harry Potter", "J.K. Rowling", "Fantasy");
        testBook2 = new Book("The Hobbit", "J.R.R. Tolkien", "Fantasy");
        
        entityManager.persist(testBook1);
        entityManager.persist(testBook2);
        entityManager.flush();
    }

    @Test
    void testFindByTitleContainingIgnoreCase() {
        List<Book> found = bookRepository.findByTitleContainingIgnoreCase("harry");
        
        assertEquals(1, found.size());
        assertEquals("Harry Potter", found.get(0).getTitle());
    }

    @Test
    void testFindByAuthorContainingIgnoreCase() {
        List<Book> found = bookRepository.findByAuthorContainingIgnoreCase("tolkien");
        
        assertEquals(1, found.size());
        assertEquals("J.R.R. Tolkien", found.get(0).getAuthor());
    }

    @Test
    void testFindByGenreContainingIgnoreCase() {
        List<Book> found = bookRepository.findByGenreContainingIgnoreCase("fantasy");
        
        assertEquals(2, found.size());
    }

    @Test
    void testFindByAuthorAndIdNot() {
        List<Book> found = bookRepository.findByAuthorAndIdNot(
            "J.R.R. Tolkien", 
            testBook2.getId(), 
            PageRequest.of(0, 10)
        );
        
        assertEquals(0, found.size());
    }

    @Test
    void testFindByGenreAndIdNot() {
        List<Book> found = bookRepository.findByGenreAndIdNot(
            "Fantasy", 
            testBook1.getId(), 
            PageRequest.of(0, 10)
        );
        
        assertEquals(1, found.size());
        assertEquals("The Hobbit", found.get(0).getTitle());
    }

    @Test
    void testSaveBook() {
        Book newBook = new Book("1984", "George Orwell", "Dystopian");
        Book saved = bookRepository.save(newBook);
        
        assertNotNull(saved.getId());
        assertEquals("1984", saved.getTitle());
    }

    @Test
    void testDeleteBook() {
        Long bookId = testBook1.getId();
        bookRepository.deleteById(bookId);
        
        assertFalse(bookRepository.findById(bookId).isPresent());
    }

    @Test
    void testFindAll() {
        List<Book> books = bookRepository.findAll();
        
        assertTrue(books.size() >= 2);
    }

    @Test
    void testFindById() {
        Long bookId = testBook1.getId();
        Book found = bookRepository.findById(bookId).orElse(null);
        
        assertNotNull(found);
        assertEquals("Harry Potter", found.getTitle());
    }
}
