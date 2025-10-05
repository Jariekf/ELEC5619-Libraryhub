package com.example.libraryhub.controller;

import com.example.libraryhub.service.RecommendationService;
import com.example.libraryhub.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final ReservationService reservationService;
    private final RecommendationService recommendationService;

    @GetMapping("/dashboard")
    public String customerDashboard(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return "redirect:/auth/login";
        }
        model.addAttribute("reservations", reservationService.getReservationsForUser(userDetails.getUsername()));
        model.addAttribute("recommended", recommendationService.recommendTop5ByAuthorsAndGenres(userDetails.getUsername()));
        return "customer/dashboard";
    }

    @GetMapping("/customer/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails user, Model model) {
        if (user == null) return "redirect:/auth/login";
        model.addAttribute("reservations",
                reservationService.getReservationsForUser(user.getUsername()));
        return "customer/dashboard";
    }

    @GetMapping("/my-reservations")
    public String myReservations(@AuthenticationPrincipal UserDetails user, Model model) {
        return dashboard(user, model);
    }


}