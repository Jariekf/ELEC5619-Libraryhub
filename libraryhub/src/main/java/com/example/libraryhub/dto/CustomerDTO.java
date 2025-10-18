package com.example.libraryhub.dto;

import com.example.libraryhub.model.User;
import com.example.libraryhub.model.Reservation;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class CustomerDTO {
    private Long id;
    private String username;
    private String email;
    private String role;
    private List<ReservationInfo> reservations;

    @Data
    public static class ReservationInfo {
        private Long id;
        private LocalDateTime reservationDate;
        private BookInfo book;
    }

    @Data
    public static class BookInfo {
        private Long id;
        private String title;
        private String author;
        private String genre;
    }

    public static CustomerDTO fromEntity(User user, List<Reservation> reservations) {
        CustomerDTO dto = new CustomerDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole().toString());

        if (reservations != null && !reservations.isEmpty()) {
            dto.setReservations(reservations.stream()
                    .map(r -> {
                        ReservationInfo resInfo = new ReservationInfo();
                        resInfo.setId(r.getId());
                        resInfo.setReservationDate(r.getReservationDate());

                        if (r.getBook() != null) {
                            BookInfo bookInfo = new BookInfo();
                            bookInfo.setId(r.getBook().getId());
                            bookInfo.setTitle(r.getBook().getTitle());
                            bookInfo.setAuthor(r.getBook().getAuthor());
                            bookInfo.setGenre(r.getBook().getGenre());
                            resInfo.setBook(bookInfo);
                        }

                        return resInfo;
                    })
                    .collect(Collectors.toList())
            );
        } else {
            dto.setReservations(List.of());
        }

        return dto;
    }
}