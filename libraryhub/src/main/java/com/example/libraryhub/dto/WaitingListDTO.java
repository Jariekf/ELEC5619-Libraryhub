package com.example.libraryhub.dto;

import com.example.libraryhub.model.WaitingList;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WaitingListDTO {
    private Long id;
    private Long bookId;
    private String bookTitle;
    private Long userId;
    private String username;
    private LocalDateTime joinedDate;

    public static WaitingListDTO fromEntity(WaitingList waitingList) {
        return WaitingListDTO.builder()
                .id(waitingList.getId())
                .bookId(waitingList.getBook() != null ? waitingList.getBook().getId() : null)
                .bookTitle(waitingList.getBook() != null ? waitingList.getBook().getTitle() : null)
                .userId(waitingList.getUser() != null ? waitingList.getUser().getId() : null)
                .username(waitingList.getUser() != null ? waitingList.getUser().getUsername() : "Unknown")
                .joinedDate(waitingList.getJoinDate())  // Use getJoinDate()
                .build();
    }
}

