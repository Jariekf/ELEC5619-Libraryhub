package com.example.libraryhub.repository;

import com.example.libraryhub.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByTitleContainingIgnoreCase(String title);
    List<Book> findByAuthorContainingIgnoreCase(String author);
    List<Book> findByGenreContainingIgnoreCase(String genre);
    List<Book> findByAuthorAndIdNot(String author, Long excludeId, Pageable pageable);
    List<Book> findByGenreAndIdNot(String genre, Long excludeId, Pageable pageable);
}
