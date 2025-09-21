package com.example.libraryhub.config;

import com.example.libraryhub.model.Admin;
import com.example.libraryhub.model.User;
import com.example.libraryhub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserRepository userRepository;

    /** Password encoder for hashing user passwords. */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /** Bridge AuthenticationManager out of Spring Boot auto config. */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception {
        return cfg.getAuthenticationManager();
    }

    /** Load user from database and map to Spring Security UserDetails. */
    @Bean
    public UserDetailsService userDetailsService() {
        return (String username) -> {
            User u = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            // Set authorities by role; here we use ADMIN for Admin subclass, otherwise
            // CUSTOMER
            String role = (u instanceof Admin) ? "ADMIN" : "CUSTOMER";
            UserDetails details = org.springframework.security.core.userdetails.User
                    .withUsername(u.getUsername())
                    .password(u.getPasswordHash()) // ensure your User has getPasswordHash()
                    .roles(role) // results in "ROLE_ADMIN"/"ROLE_CUSTOMER"
                    .disabled(!u.isActive())
                    .build();
            return details;
        };
    }

    /**
     * HTTP security: allow auth pages, require login elsewhere, enable form login +
     * logout.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.ignoringRequestMatchers("/auth/**")) // simple forms; tune later
                .authorizeHttpRequests(reg -> reg
                        .requestMatchers("/", "/auth/**", "/css/**", "/js/**").permitAll()
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/auth/login").permitAll()
                        .loginProcessingUrl("/auth/login") // POST endpoint handled by Spring
                        .defaultSuccessUrl("/books", true)
                        .failureUrl("/auth/login?error"))
                .logout(logout -> logout
                        .logoutUrl("/auth/logout") // POST /auth/logout
                        .logoutSuccessUrl("/auth/login?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll());
        return http.build();
    }
}
