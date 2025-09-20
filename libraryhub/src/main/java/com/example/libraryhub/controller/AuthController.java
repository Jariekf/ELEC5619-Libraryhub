package com.example.libraryhub.controller;

import com.example.libraryhub.dto.RegisterRequest;
import com.example.libraryhub.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/** MVC endpoints for registration and login pages. */
@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /** Render login page. Handled by Spring Security on POST. */
    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    /** Render registration page. */
    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("form", new RegisterRequest());
        return "auth/register";
    }

    /** Handle registration form submit, then redirect to login. */
    @PostMapping("/register")
    public String doRegister(@ModelAttribute("form") RegisterRequest form, Model model) {
        try {
            authService.registerCustomer(form);
            return "redirect:/auth/login?registered";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("form", form);
            model.addAttribute("error", ex.getMessage());
            return "auth/register";
        }
    }
}
