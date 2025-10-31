package com.example.libraryhub.controller;

import com.example.libraryhub.model.Role;
import com.example.libraryhub.model.User;
import com.example.libraryhub.service.BookService;
import com.example.libraryhub.service.ReservationService;
import com.example.libraryhub.repository.UserRepository;
import com.example.libraryhub.service.WaitingListService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;
    private final ReservationService reservationService;
    private final WaitingListService waitingListService;
    private final BookService bookService;

    @GetMapping("/dashboard")
    public String adminDashboard() {
        return "admin/dashboard";
    }

    @GetMapping("/reservations")
    public String listReservations(Model model) {
        model.addAttribute("reservations", reservationService.getAllReservations());
        return "admin/reservations";
    }

    @GetMapping("/customers")
    public String listCustomers(Model model) {
        List<User> customers = userRepository.findAll().stream()
                .filter(user -> user.getRole() == Role.CUSTOMER)
                .collect(Collectors.toList());
        model.addAttribute("customers", customers);
        return "admin/customers";
    }

    @PostMapping("/books/{bookId}/waitinglist/{entryId}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteWaitingEntry(@PathVariable Long bookId,
                                     @PathVariable Long entryId,
                                     RedirectAttributes ra) {
        try {
            waitingListService.deleteWaitingEntry(entryId);
            ra.addFlashAttribute("successMessage", "Waiting list entry deleted.");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/books/" + bookId;
    }

    @PostMapping("/books/{bookId}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteBook(@PathVariable Long bookId, RedirectAttributes ra) {
        try {
            bookService.deleteById(bookId); // will cascade remove waiting list entries
            ra.addFlashAttribute("successMessage", "Book and its waiting list have been removed.");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/dashboard";
    }
}
