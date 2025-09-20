package com.example.libraryhub.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;

import com.example.libraryhub.model.Role;
import java.util.ArrayList;
import java.util.List;

/**
 * Library customer: can make/cancel reservations, join waiting lists,
 * and write/delete reviews.
 */
@Getter
@NoArgsConstructor
@ToString(callSuper = true)
@Entity
@DiscriminatorValue("CUSTOMER")
public class Customer extends User {

    /** Simple placeholders for demo; replace with entities later. */
    private final List<String> reservationHistory = new ArrayList<>();
    private final List<String> waitingListItems   = new ArrayList<>();
    private final List<String> reviews            = new ArrayList<>();

    public Customer(Long id, String username, String email, String passwordHash) {
        super(id, username, email, passwordHash, true, java.time.LocalDateTime.now());
    }

    @Override
    public com.example.libraryhub.model.Role getRole() { return Role.CUSTOMER; }

    // --- Domain actions (signatures kept simple; move real logic to services) ---
    public void makeReservation(String bookId)   { reservationHistory.add("RESERVE:" + bookId); }
    public void cancelReservation(String bookId) { reservationHistory.add("CANCEL:" + bookId); }
    public void returnBook(String bookId)        { reservationHistory.add("RETURN:" + bookId); }
    public void joinWaitingList(String bookId)   { waitingListItems.add(bookId); }
    public void quitWaitingList(String bookId)   { waitingListItems.remove(bookId); }
    public void writeReview(String bookId, String content) {
        reviews.add("BOOK:" + bookId + " -> " + content);
    }
    public void deleteReview(int index) {
        if (index >= 0 && index < reviews.size()) reviews.remove(index);
    }
}