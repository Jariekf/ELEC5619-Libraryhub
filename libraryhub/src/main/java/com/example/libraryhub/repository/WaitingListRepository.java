package com.example.libraryhub.repository;

import com.example.libraryhub.model.Book;
import com.example.libraryhub.model.User;
import com.example.libraryhub.model.WaitingList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WaitingListRepository extends JpaRepository<WaitingList, Long> {

    // Check whether a user is already in the waiting list for a specific book
    boolean existsByBookAndUser(Book book, User user);

    // Get all waiting list entries for a book entity, ordered by join date
    List<WaitingList> findByBookOrderByJoinDateAsc(Book book);

    // Get all waiting list entries for a bookId, ordered by join date
    List<WaitingList> findByBookIdOrderByJoinDateAsc(Long bookId);

    // Get the earliest waiting list entry for a bookId (null if none)
    WaitingList findFirstByBookIdOrderByJoinDateAsc(Long bookId);

    // Remove the waiting list entry for a given (book, user) pair
    void deleteByBookAndUser(Book book, User user);
}
