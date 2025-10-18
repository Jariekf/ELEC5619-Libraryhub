package com.example.libraryhub.controller;

import com.example.libraryhub.dto.AdminCreateUserRequest;
import com.example.libraryhub.dto.AdminUpdateUserRequest;
import com.example.libraryhub.dto.AdminUserResponse;
import com.example.libraryhub.service.AdminAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST endpoints for admin-driven user management.
 */
@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"}, allowCredentials = "true")
public class AdminUserRestController {

    private final AdminAccountService adminAccountService;

    @GetMapping
    public List<AdminUserResponse> listUsers() {
        return adminAccountService.getAllUsers();
    }

    @PostMapping
    public ResponseEntity<AdminUserResponse> createUser(@RequestBody AdminCreateUserRequest request) {
        AdminUserResponse created = adminAccountService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public AdminUserResponse updateUser(@PathVariable Long id, @RequestBody AdminUpdateUserRequest request) {
        return adminAccountService.updateUser(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id,
                                           @AuthenticationPrincipal UserDetails principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        adminAccountService.deleteUser(id, principal.getUsername());
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException ex) {
        Map<String, String> body = new HashMap<>();
        body.put("error", ex.getMessage());
        return ResponseEntity.badRequest().body(body);
    }
}

