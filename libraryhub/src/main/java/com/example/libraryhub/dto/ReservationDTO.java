package com.example.libraryhub.dto;

import com.example.libraryhub.model.Reservation;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReservationDTO {
    private Long id;
    private LocalDateTime reservationDate;
    private BookDTO book;
    private UserDTO user;

    @Data
    public static class BookDTO {
        private Long id;
        private String title;
        private String author;
        private String genre;
        private boolean available;
    }

    @Data
    public static class UserDTO {
        private Long id;
        private String username;
        private String email;
        private String role;
    }

    public static ReservationDTO fromEntity(Reservation reservation) {
        ReservationDTO dto = new ReservationDTO();
        dto.setId(reservation.getId());
        dto.setReservationDate(reservation.getReservationDate());

        if (reservation.getBook() != null) {
            BookDTO bookDTO = new BookDTO();
            bookDTO.setId(reservation.getBook().getId());
            bookDTO.setTitle(reservation.getBook().getTitle());
            bookDTO.setAuthor(reservation.getBook().getAuthor());
            bookDTO.setGenre(reservation.getBook().getGenre());
            bookDTO.setAvailable(reservation.getBook().isAvailable());
            dto.setBook(bookDTO);
        }

        if (reservation.getUser() != null) {
            UserDTO userDTO = new UserDTO();
            userDTO.setId(reservation.getUser().getId());
            userDTO.setUsername(reservation.getUser().getUsername());
            userDTO.setEmail(reservation.getUser().getEmail());
            userDTO.setRole(reservation.getUser().getRole().toString());
            dto.setUser(userDTO);
        }

        return dto;
    }
}