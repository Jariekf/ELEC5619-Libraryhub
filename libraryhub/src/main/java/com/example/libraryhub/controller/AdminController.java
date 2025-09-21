package com.example.libraryhub.controller;

import com.example.libraryhub.service.ReservationService;
import com.example.libraryhub.repository.UserRepository; // Assuming you have this from previous context
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;
    private final ReservationService reservationService;

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
        model.addAttribute("customers", userRepository.findAllCustomersWithReservations());
        return "admin/customers";
    }
}