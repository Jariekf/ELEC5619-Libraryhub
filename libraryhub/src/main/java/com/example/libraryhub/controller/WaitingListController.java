package com.example.libraryhub.controller;

import com.example.libraryhub.model.WaitingList;
import com.example.libraryhub.service.WaitingListService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@RestController
@RequestMapping("/api/waitinglist")
public class WaitingListController {

    @Autowired
    private WaitingListService waitingListService;

    @PostMapping("/book/{bookId}")
    public ResponseEntity<WaitingList> addToWaitingList(@PathVariable Long bookId,
                                                        @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        WaitingList entry = waitingListService.addToWaitingList(bookId, userDetails.getUsername());
        return ResponseEntity.ok(entry);
    }

    @GetMapping("/book/{bookId}")
    public ResponseEntity<List<WaitingList>> getWaitingList(@PathVariable Long bookId) {
        return ResponseEntity.ok(waitingListService.getWaitingList(bookId));
    }

    @DeleteMapping("/book/{bookId}")
    public ResponseEntity<Void> cancelWaiting(@PathVariable Long bookId,
                                              @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        waitingListService.cancelWaiting(bookId, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}

